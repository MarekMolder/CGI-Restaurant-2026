import { useState, useEffect, useRef, useCallback } from 'react';
import { Link } from 'react-router-dom';
import { listZones } from '../api/zones';
import { listByZone, updateLayout } from '../api/tableEntities';
import { useAuth } from '../context/AuthContext';
import Card from '../components/Card';
import indoorImage from '../assets/indoor.png';
import outsideImage from '../assets/outside.png';
import privateImage from '../assets/private.png';

const ZONE_TYPE_IMAGE = {
  INDOOR: indoorImage,
  TERRACE: outsideImage,
  PRIVATE: privateImage,
};
const DEFAULT_ZONE_IMAGE = indoorImage;

const PLAN_WIDTH = 800;
const PLAN_HEIGHT = 600;
const MIN_SIZE = 20;
const ROTATE_HANDLE_OFFSET = -18;

function tableStyle(shape) {
  const base = 'border-2 border-restaurant-gold/70 bg-restaurant-card/95 shadow-lg flex flex-col items-center justify-center text-restaurant-gold font-medium select-none';
  if (shape === 'CIRCLE') return `${base} rounded-full`;
  if (shape === 'OVAL') return `${base} rounded-[50%]`;
  return `${base} rounded-lg`;
}

function DraggableTable({ table, planWidth, planHeight, onLayoutChange, containerRef }) {
  const [mode, setMode] = useState(null); // 'drag' | 'resize' | 'rotate'
  const [local, setLocal] = useState({
    x: table.x,
    y: table.y,
    width: table.width,
    height: table.height,
    rotationDegree: table.rotationDegree ?? 0,
  });
  const lastRef = useRef({ ...local });
  const startRef = useRef({});

  useEffect(() => {
    lastRef.current = local;
  }, [local]);

  useEffect(() => {
    setLocal({
      x: table.x,
      y: table.y,
      width: table.width,
      height: table.height,
      rotationDegree: table.rotationDegree ?? 0,
    });
  }, [table.x, table.y, table.width, table.height, table.rotationDegree]);

  const toPlan = useCallback(
    (clientX, clientY) => {
      if (!containerRef?.current) return { x: 0, y: 0 };
      const rect = containerRef.current.getBoundingClientRect();
      return {
        x: ((clientX - rect.left) / rect.width) * planWidth,
        y: ((clientY - rect.top) / rect.height) * planHeight,
      };
    },
    [containerRef, planWidth, planHeight]
  );

  const handleDragStart = useCallback(
    (e) => {
      e.preventDefault();
      if (!containerRef?.current) return;
      const plan = toPlan(e.clientX, e.clientY);
      startRef.current = { mouseX: e.clientX, mouseY: e.clientY, ...local };
      setMode('drag');
    },
    [containerRef, local, toPlan]
  );

  const handleResizeStart = useCallback(
    (e) => {
      e.preventDefault();
      e.stopPropagation();
      startRef.current = { ...local };
      setMode('resize');
    },
    [local]
  );

  const handleRotateStart = useCallback(
    (e) => {
      e.preventDefault();
      e.stopPropagation();
      startRef.current = { ...local, mouseX: e.clientX, mouseY: e.clientY };
      setMode('rotate');
    },
    [local]
  );

  useEffect(() => {
    if (!mode) return;
    const rect = containerRef?.current?.getBoundingClientRect();
    if (!rect) return;

    const onMove = (e) => {
      const plan = toPlan(e.clientX, e.clientY);
      const start = startRef.current;

      if (mode === 'drag') {
        const dx = (e.clientX - start.mouseX) / rect.width * planWidth;
        const dy = (e.clientY - start.mouseY) / rect.height * planHeight;
        const newX = Math.max(0, Math.min(planWidth - start.width, start.x + dx));
        const newY = Math.max(0, Math.min(planHeight - start.height, start.y + dy));
        setLocal((prev) => ({ ...prev, x: newX, y: newY }));
      } else if (mode === 'resize') {
        const newW = Math.max(MIN_SIZE, Math.min(planWidth - start.x, plan.x - start.x));
        const newH = Math.max(MIN_SIZE, Math.min(planHeight - start.y, plan.y - start.y));
        if (newW >= MIN_SIZE && newH >= MIN_SIZE) {
          setLocal((prev) => ({ ...prev, width: newW, height: newH }));
        }
      } else if (mode === 'rotate') {
        const cx = start.x + start.width / 2;
        const cy = start.y + start.height / 2;
        const cxPx = (cx / planWidth) * rect.width + rect.left;
        const cyPx = (cy / planHeight) * rect.height + rect.top;
        const angle = Math.atan2(e.clientY - cyPx, e.clientX - cxPx);
        const startAngle = Math.atan2(start.mouseY - cyPx, start.mouseX - cxPx);
        let deltaDeg = (angle - startAngle) * (180 / Math.PI);
        let newRot = start.rotationDegree + deltaDeg;
        newRot = ((newRot % 360) + 360) % 360;
        setLocal((prev) => ({ ...prev, rotationDegree: Math.round(newRot) }));
      }
    };

    const onUp = () => {
      const current = lastRef.current;
      setMode(null);
      const same =
        Math.abs(current.x - table.x) < 0.01 &&
        Math.abs(current.y - table.y) < 0.01 &&
        Math.abs(current.width - table.width) < 0.01 &&
        Math.abs(current.height - table.height) < 0.01 &&
        Math.abs((current.rotationDegree ?? 0) - (table.rotationDegree ?? 0)) < 0.01;
      if (same) return;
      onLayoutChange(table.id, {
        x: Math.round(current.x * 10) / 10,
        y: Math.round(current.y * 10) / 10,
        width: Math.round(current.width * 10) / 10,
        height: Math.round(current.height * 10) / 10,
        rotationDegree: Math.round(current.rotationDegree ?? 0),
      }).catch(() => {
        setLocal({
          x: table.x,
          y: table.y,
          width: table.width,
          height: table.height,
          rotationDegree: table.rotationDegree ?? 0,
        });
      });
    };

    window.addEventListener('mousemove', onMove);
    window.addEventListener('mouseup', onUp);
    return () => {
      window.removeEventListener('mousemove', onMove);
      window.removeEventListener('mouseup', onUp);
    };
  }, [mode, table.id, table.x, table.y, table.width, table.height, table.rotationDegree, onLayoutChange, containerRef, toPlan, planWidth, planHeight]);

  const { x, y, width, height, rotationDegree } = local;
  const leftPct = (x / planWidth) * 100;
  const topPct = (y / planHeight) * 100;
  const widthPct = (width / planWidth) * 100;
  const heightPct = (height / planHeight) * 100;
  const rot = rotationDegree ?? 0;
  const isActive = mode !== null;

  return (
    <div
      className={`absolute ${tableStyle(table.shape)} ${isActive ? 'z-20 ring-2 ring-restaurant-gold' : ''}`}
      style={{
        left: `${leftPct}%`,
        top: `${topPct}%`,
        width: `${widthPct}%`,
        height: `${heightPct}%`,
        minWidth: '24px',
        minHeight: '24px',
        fontSize: 'clamp(8px, 1.2vw, 12px)',
        transform: `rotate(${rot}deg)`,
        transformOrigin: '0 0',
      }}
      onMouseDown={handleDragStart}
      onClick={(e) => e.stopPropagation()}
    >
      {/* Rotate handle – laua kohal */}
      <div
        className="absolute left-1/2 -translate-x-1/2 cursor-grab active:cursor-grabbing w-6 h-4 flex items-center justify-center text-restaurant-gold hover:bg-restaurant-gold/20 rounded"
        style={{ top: ROTATE_HANDLE_OFFSET, transform: 'translate(-50%, 0)' }}
        onMouseDown={handleRotateStart}
        title="Pööra"
      >
        <span className="text-xs" aria-hidden>↻</span>
      </div>
      <span className="truncate w-full text-center px-0.5">{table.label}</span>
      <span className="text-stone-400 text-[0.65em]">{table.capacity}</span>
      {/* Resize handle – parem alumine nurk */}
      <div
        className="absolute bottom-0 right-0 w-3 h-3 cursor-nwse-resize bg-restaurant-gold/60 hover:bg-restaurant-gold rounded-br border border-restaurant-gold"
        onMouseDown={handleResizeStart}
        title="Suurenda / vähenda"
      />
    </div>
  );
}

export default function TableDrag() {
  const { userEmail, logout } = useAuth();
  const [zones, setZones] = useState([]);
  const [zoneId, setZoneId] = useState('');
  const [tables, setTables] = useState([]);
  const [loading, setLoading] = useState(true);
  const [tablesLoading, setTablesLoading] = useState(false);
  const [error, setError] = useState('');
  const overlayRef = useRef(null);
  const overlayRefMobile = useRef(null);

  useEffect(() => {
    let cancelled = false;
    setError('');
    listZones()
      .then((z) => {
        if (!cancelled) {
          setZones(z);
          if (z.length > 0 && !zoneId) setZoneId(z[0].id);
        }
      })
      .catch((err) => {
        if (!cancelled) setError(err.message || 'Tsoone ei saadud');
      })
      .finally(() => {
        if (!cancelled) setLoading(false);
      });
    return () => { cancelled = true; };
  }, []);

  useEffect(() => {
    if (!zoneId) {
      setTables([]);
      return;
    }
    let cancelled = false;
    setTablesLoading(true);
    listByZone(zoneId)
      .then((list) => {
        if (!cancelled) setTables(Array.isArray(list) ? list : []);
      })
      .catch(() => {
        if (!cancelled) setTables([]);
      })
      .finally(() => {
        if (!cancelled) setTablesLoading(false);
      });
    return () => { cancelled = true; };
  }, [zoneId]);

  const handleLayoutChange = useCallback(async (id, layout) => {
    const updated = await updateLayout(
      id,
      layout.x,
      layout.y,
      layout.width,
      layout.height,
      layout.rotationDegree
    );
    setTables((prev) =>
      prev.map((t) =>
        t.id === id
          ? {
              ...t,
              x: updated.x,
              y: updated.y,
              width: updated.width,
              height: updated.height,
              rotationDegree: updated.rotationDegree,
            }
          : t
      )
    );
  }, []);

  const selectedZone = zones.find((z) => z.id === zoneId);
  const zoneImage = selectedZone
    ? (ZONE_TYPE_IMAGE[selectedZone.type] ?? DEFAULT_ZONE_IMAGE)
    : null;
  const displayImage = zoneImage ?? DEFAULT_ZONE_IMAGE;

  const floorPlanSection = (overlayRefKey) => (
    <div className="rounded-2xl overflow-hidden border border-restaurant-border shadow-2xl bg-restaurant-card w-fit max-w-full relative min-h-[200px]">
      <img
        src={displayImage}
        alt={selectedZone ? selectedZone.name : 'Tsooni pilt'}
        className="block max-h-[90vh] max-w-full w-auto h-auto object-contain"
      />
      <div
        ref={overlayRefKey === 'desktop' ? overlayRef : overlayRefMobile}
        className="absolute inset-0"
      >
        {tablesLoading
          ? null
          : tables.map((t) => (
              <DraggableTable
                key={t.id}
                table={t}
                planWidth={PLAN_WIDTH}
                planHeight={PLAN_HEIGHT}
                onLayoutChange={handleLayoutChange}
                containerRef={overlayRefKey === 'desktop' ? overlayRef : overlayRefMobile}
              />
            ))}
      </div>
    </div>
  );

  return (
    <div className="min-h-screen bg-restaurant-dark flex flex-col">
      <header className="border-b border-restaurant-border bg-restaurant-card/90 backdrop-blur-md shrink-0 sticky top-0 z-10">
        <div className="max-w-7xl mx-auto px-4 py-3 flex items-center justify-between">
          <Link
            to="/"
            className="text-restaurant-gold hover:text-restaurant-goldLight transition font-display inline-flex items-center gap-2"
          >
            ← Avaleht
          </Link>
          <div className="flex items-center gap-3">
            <span className="text-stone-500 text-sm hidden sm:inline truncate max-w-[160px]">{userEmail}</span>
            <button
              type="button"
              onClick={logout}
              className="text-stone-400 hover:text-restaurant-gold text-sm transition"
            >
              Logi välja
            </button>
          </div>
        </div>
      </header>

      <div className="flex-1 flex flex-col lg:flex-row min-h-0">
        <div className="flex-1 lg:flex-[1.6] lg:min-w-0 lg:min-h-0 lg:overflow-auto py-8 px-6 lg:px-16 lg:border-r lg:border-restaurant-border">
          <h1 className="font-display text-3xl text-restaurant-gold mb-2">Laudade asukohad</h1>
          <p className="text-stone-500 mb-6">Lohista laudu, suurenda/vähenda nurga punniga, pööra noolega üleval</p>

          {loading && <p className="text-stone-400">Laen tsoone…</p>}
          {error && (
            <div className="mb-6 p-4 rounded-lg bg-amber-500/10 border border-amber-500/30 text-amber-400 text-sm">
              {error}
            </div>
          )}

          {!loading && (
            <Card title="Tsoon">
              <div>
                <label className="block text-sm font-medium text-stone-400 mb-2">Tsoon</label>
                <select
                  name="zoneId"
                  value={zoneId}
                  onChange={(e) => setZoneId(e.target.value)}
                  className="w-full px-4 py-2.5 rounded-lg bg-restaurant-card border border-restaurant-border text-stone-200 focus:outline-none focus:ring-2 focus:ring-restaurant-gold focus:border-transparent"
                >
                  <option value="">Vali tsoon</option>
                  {zones.map((z) => (
                    <option key={z.id} value={z.id}>
                      {z.name}
                    </option>
                  ))}
                </select>
              </div>
            </Card>
          )}
        </div>

        <div className="hidden lg:flex lg:flex-1 lg:min-h-0 lg:items-center lg:justify-end lg:py-8 lg:pl-6 lg:pr-6 xl:pr-8">
          <div className="max-h-[90vh] max-w-full [&>div]:max-h-[90vh] [&>div]:max-w-full">
            {floorPlanSection('desktop')}
          </div>
        </div>

        <div className="lg:hidden mt-6 mx-4 flex items-center justify-center min-h-[220px] py-4">
          <div className="rounded-xl overflow-hidden border border-restaurant-border w-full max-w-full max-h-80 [&>div]:max-h-80">
            {floorPlanSection('mobile')}
          </div>
        </div>
      </div>
    </div>
  );
}
