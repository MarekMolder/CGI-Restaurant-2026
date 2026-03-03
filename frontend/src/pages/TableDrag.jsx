import { useState, useEffect, useRef, useCallback } from 'react';
import { Link } from 'react-router-dom';
import { listZones } from '../api/zones';
import { listByZone, updatePosition } from '../api/tableEntities';
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

function tableStyle(shape) {
  const base = 'border-2 border-restaurant-gold/70 bg-restaurant-card/95 shadow-lg flex flex-col items-center justify-center text-restaurant-gold font-medium select-none';
  if (shape === 'CIRCLE') return `${base} rounded-full`;
  if (shape === 'OVAL') return `${base} rounded-[50%]`;
  return `${base} rounded-lg`;
}

function DraggableTable({ table, planWidth, planHeight, onDragEnd, containerRef }) {
  const [isDragging, setIsDragging] = useState(false);
  const [dragOffset, setDragOffset] = useState({ x: 0, y: 0 });
  const [localPos, setLocalPos] = useState({ x: table.x, y: table.y });
  const lastPosRef = useRef({ x: table.x, y: table.y });

  useEffect(() => {
    lastPosRef.current = localPos;
  }, [localPos]);

  useEffect(() => {
    setLocalPos({ x: table.x, y: table.y });
  }, [table.x, table.y]);

  const handleMouseDown = useCallback(
    (e) => {
      e.preventDefault();
      if (!containerRef?.current) return;
      const rect = containerRef.current.getBoundingClientRect();
      setDragOffset({
        x: (table.x / planWidth) * rect.width - (e.clientX - rect.left),
        y: (table.y / planHeight) * rect.height - (e.clientY - rect.top),
      });
      setIsDragging(true);
    },
    [table.x, table.y, planWidth, planHeight, containerRef]
  );

  useEffect(() => {
    if (!isDragging) return;
    const rect = containerRef?.current?.getBoundingClientRect();
    if (!rect) return;

    const onMove = (e) => {
      const left = e.clientX - rect.left + dragOffset.x;
      const top = e.clientY - rect.top + dragOffset.y;
      const planX = (left / rect.width) * planWidth;
      const planY = (top / rect.height) * planHeight;
      const clampedX = Math.max(0, Math.min(planWidth - table.width, planX));
      const clampedY = Math.max(0, Math.min(planHeight - table.height, planY));
      setLocalPos({ x: clampedX, y: clampedY });
    };
    const onUp = () => {
      setIsDragging(false);
      const { x: newX, y: newY } = lastPosRef.current;
      const roundedX = Math.round(newX * 10) / 10;
      const roundedY = Math.round(newY * 10) / 10;
      if (Math.abs(roundedX - table.x) < 0.01 && Math.abs(roundedY - table.y) < 0.01) return;
      onDragEnd(table.id, roundedX, roundedY).catch(() => {
        setLocalPos({ x: table.x, y: table.y });
      });
    };

    window.addEventListener('mousemove', onMove);
    window.addEventListener('mouseup', onUp);
    return () => {
      window.removeEventListener('mousemove', onMove);
      window.removeEventListener('mouseup', onUp);
    };
  }, [isDragging, dragOffset, planWidth, planHeight, table.width, table.height, table.id, table.x, table.y, onDragEnd, containerRef]);

  const x = isDragging ? localPos.x : table.x;
  const y = isDragging ? localPos.y : table.y;

  const leftPct = (x / planWidth) * 100;
  const topPct = (y / planHeight) * 100;
  const widthPct = (table.width / planWidth) * 100;
  const heightPct = (table.height / planHeight) * 100;

  return (
    <div
      role="button"
      tabIndex={0}
      className={`absolute cursor-grab active:cursor-grabbing ${tableStyle(table.shape)} ${isDragging ? 'z-20 ring-2 ring-restaurant-gold' : ''}`}
      style={{
        left: `${leftPct}%`,
        top: `${topPct}%`,
        width: `${widthPct}%`,
        height: `${heightPct}%`,
        minWidth: '24px',
        minHeight: '24px',
        fontSize: 'clamp(8px, 1.2vw, 12px)',
      }}
      onMouseDown={handleMouseDown}
      onClick={(e) => e.stopPropagation()}
    >
      <span className="truncate w-full text-center px-0.5">{table.label}</span>
      <span className="text-stone-400 text-[0.65em]">{table.capacity}</span>
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

  const handleDragEnd = useCallback(async (id, x, y) => {
    const updated = await updatePosition(id, x, y);
    setTables((prev) =>
      prev.map((t) => (t.id === id ? { ...t, x: updated.x, y: updated.y } : t))
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
                onDragEnd={handleDragEnd}
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
          <p className="text-stone-500 mb-6">Vali tsoon ja lohista laudu pildil</p>

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
