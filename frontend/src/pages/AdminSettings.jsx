import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { listZones } from '../api/zones';
import { listFeatures } from '../api/features';
import {
  listAll,
  getById,
  createTable,
  updateTable,
  deleteTable,
} from '../api/tableEntities';
import { useAuth } from '../context/AuthContext';
import Card from '../components/Card';
import Button from '../components/Button';
import Input from '../components/Input';

const SHAPES = [
  { value: 'RECT', label: 'Ristkülik' },
  { value: 'CIRCLE', label: 'Ring' },
  { value: 'OVAL', label: 'Ovaal' },
];

const defaultForm = () => ({
  label: '',
  capacity: 2,
  minPartySize: 1,
  shape: 'RECT',
  x: 100,
  y: 100,
  width: 80,
  height: 70,
  rotationDegree: 0,
  active: true,
  zoneId: '',
  featureIds: [],
});

export default function AdminSettings() {
  const { isAdmin } = useAuth();
  const [zones, setZones] = useState([]);
  const [features, setFeatures] = useState([]);
  const [tables, setTables] = useState([]);
  const [totalElements, setTotalElements] = useState(0);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [form, setForm] = useState(defaultForm());
  const [editingId, setEditingId] = useState(null);
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    let cancelled = false;
    Promise.all([listZones(), listFeatures()])
      .then(([z, f]) => {
        if (!cancelled) {
          setZones(z);
          setFeatures(f);
          if (z.length > 0 && !form.zoneId) setForm((prev) => ({ ...prev, zoneId: z[0].id }));
        }
      })
      .catch((e) => !cancelled && setError(e.message || 'Andmeid ei saadud'))
      .finally(() => !cancelled && setLoading(false));
    return () => { cancelled = true; };
  }, []);

  const loadTables = () => {
    setError('');
    listAll(0, 200)
      .then((page) => {
        setTables(page.content || []);
        setTotalElements(page.totalElements ?? 0);
      })
      .catch((e) => setError(e.message || 'Laudu ei saadud'));
  };

  useEffect(() => {
    loadTables();
  }, []);

  const handleCreate = (e) => {
    e.preventDefault();
    setError('');
    setSubmitting(true);
    const payload = {
      ...form,
      zoneId: form.zoneId || undefined,
      featureIds: Array.isArray(form.featureIds) ? form.featureIds : [],
      adjacentTableIds: [],
    };
    createTable(payload)
      .then(() => {
        setForm(defaultForm());
        if (zones.length > 0) setForm((prev) => ({ ...prev, zoneId: zones[0].id }));
        loadTables();
      })
      .catch((e) => setError(e.message || 'Loomine ebaõnnestus'))
      .finally(() => setSubmitting(false));
  };

  const handleEdit = (id) => {
    setError('');
    getById(id)
      .then((t) => {
        setForm({
          label: t.label ?? '',
          capacity: t.capacity ?? 2,
          minPartySize: t.minPartySize ?? 1,
          shape: t.shape ?? 'RECT',
          x: t.x ?? 100,
          y: t.y ?? 100,
          width: t.width ?? 80,
          height: t.height ?? 70,
          rotationDegree: t.rotationDegree ?? 0,
          active: t.active ?? true,
          zoneId: t.zoneId ?? '',
          featureIds: Array.isArray(t.featureIds) ? t.featureIds : [],
        });
        setEditingId(id);
      })
      .catch((e) => setError(e.message || 'Laadi ebaõnnestus'));
  };

  const handleUpdate = (e) => {
    e.preventDefault();
    if (!editingId) return;
    setError('');
    setSubmitting(true);
    const payload = {
      id: editingId,
      ...form,
      zoneId: form.zoneId || undefined,
      featureIds: Array.isArray(form.featureIds) ? form.featureIds : [],
      adjacentTableIds: [],
    };
    updateTable(editingId, payload)
      .then(() => {
        setEditingId(null);
        setForm(defaultForm());
        loadTables();
      })
      .catch((e) => setError(e.message || 'Uuendamine ebaõnnestus'))
      .finally(() => setSubmitting(false));
  };

  const handleDelete = (id, label) => {
    if (!window.confirm(`Kustuta laud "${label}"?`)) return;
    setError('');
    deleteTable(id)
      .then(() => {
        if (editingId === id) setEditingId(null);
        setForm(defaultForm());
        loadTables();
      })
      .catch((e) => setError(e.message || 'Kustutamine ebaõnnestus'));
  };

  const cancelEdit = () => {
    setEditingId(null);
    setForm(defaultForm());
  };

  const zoneName = (zoneId) => zones.find((z) => z.id === zoneId)?.name ?? zoneId;
  const featureNames = (ids) =>
    (ids || [])
      .map((id) => features.find((f) => f.id === id)?.name ?? id)
      .join(', ') || '—';

  if (!isAdmin) {
    return (
      <div className="min-h-screen bg-restaurant-dark text-stone-200 flex items-center justify-center p-4">
        <Card className="max-w-md">
          <p className="text-stone-400">Selle lehe vaatamiseks on vaja administraatori õigusi.</p>
          <Link to="/" className="text-restaurant-gold mt-4 inline-block">Tagasi avalehele</Link>
        </Card>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-restaurant-dark text-stone-200">
      <header className="border-b border-restaurant-border bg-restaurant-card/90 backdrop-blur-md">
        <div className="max-w-5xl mx-auto px-4 py-3 flex items-center justify-between">
          <Link to="/" className="text-restaurant-gold font-display text-lg">CGI Restoran</Link>
          <nav className="flex gap-4">
            <Link to="/admin/table-drag" className="text-stone-400 hover:text-restaurant-gold text-sm">Laudade asukohad</Link>
            <Link to="/" className="text-stone-400 hover:text-restaurant-gold text-sm">Avaleht</Link>
          </nav>
        </div>
      </header>

      <main className="max-w-5xl mx-auto px-4 py-6">
        <h1 className="font-display text-restaurant-gold text-2xl mb-6">Seaded – laudade haldus</h1>

        {error && (
          <div className="mb-4 p-3 rounded-lg bg-amber-500/10 border border-amber-500/30 text-amber-400 text-sm">
            {error}
          </div>
        )}

        <div className="grid gap-6 lg:grid-cols-2">
          <Card className="lg:col-span-1">
            <h2 className="text-lg font-medium text-stone-200 mb-4">
              {editingId ? 'Muuda lauda' : 'Lisa uus laud'}
            </h2>
            <form
              onSubmit={editingId ? handleUpdate : handleCreate}
              className="space-y-4"
            >
              <Input
                label="Nimetus"
                value={form.label}
                onChange={(e) => setForm((p) => ({ ...p, label: e.target.value }))}
                required
                placeholder="nt T1"
              />
              <div className="grid grid-cols-2 gap-4">
                <Input
                  label="Mahutavus"
                  type="number"
                  min={1}
                  max={100}
                  value={form.capacity}
                  onChange={(e) => setForm((p) => ({ ...p, capacity: Number(e.target.value) || 1 }))}
                  required
                />
                <Input
                  label="Min. inimest"
                  type="number"
                  min={0}
                  value={form.minPartySize}
                  onChange={(e) => setForm((p) => ({ ...p, minPartySize: Number(e.target.value) || 0 }))}
                  required
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-stone-400 mb-1">Kuju</label>
                <select
                  value={form.shape}
                  onChange={(e) => setForm((p) => ({ ...p, shape: e.target.value }))}
                  className="w-full px-4 py-2.5 rounded-lg bg-restaurant-card border border-restaurant-border text-stone-200 focus:ring-2 focus:ring-restaurant-gold"
                >
                  {SHAPES.map((s) => (
                    <option key={s.value} value={s.value}>{s.label}</option>
                  ))}
                </select>
              </div>
              <div>
                <label className="block text-sm font-medium text-stone-400 mb-1">Tsoon *</label>
                <select
                  value={form.zoneId}
                  onChange={(e) => setForm((p) => ({ ...p, zoneId: e.target.value }))}
                  required
                  className="w-full px-4 py-2.5 rounded-lg bg-restaurant-card border border-restaurant-border text-stone-200 focus:ring-2 focus:ring-restaurant-gold"
                >
                  <option value="">Vali tsoon</option>
                  {zones.map((z) => (
                    <option key={z.id} value={z.id}>{z.name}</option>
                  ))}
                </select>
              </div>
              <div>
                <label className="block text-sm font-medium text-stone-400 mb-1">Funktsioonid (valikuline)</label>
                <select
                  multiple
                  value={form.featureIds}
                  onChange={(e) =>
                    setForm((p) => ({
                      ...p,
                      featureIds: Array.from(e.target.selectedOptions, (o) => o.value),
                    }))
                  }
                  className="w-full px-4 py-2.5 rounded-lg bg-restaurant-card border border-restaurant-border text-stone-200 focus:ring-2 focus:ring-restaurant-gold min-h-[80px]"
                >
                  {features.map((f) => (
                    <option key={f.id} value={f.id}>{f.name}</option>
                  ))}
                </select>
                <p className="text-stone-500 text-xs mt-1">Hoia Ctrl/Cmd all, et valida mitu</p>
              </div>
              <div className="grid grid-cols-2 gap-4">
                <Input
                  label="X"
                  type="number"
                  value={form.x}
                  onChange={(e) => setForm((p) => ({ ...p, x: Number(e.target.value) || 0 }))}
                />
                <Input
                  label="Y"
                  type="number"
                  value={form.y}
                  onChange={(e) => setForm((p) => ({ ...p, y: Number(e.target.value) || 0 }))}
                />
                <Input
                  label="Laius"
                  type="number"
                  min={1}
                  value={form.width}
                  onChange={(e) => setForm((p) => ({ ...p, width: Number(e.target.value) || 80 }))}
                />
                <Input
                  label="Kõrgus"
                  type="number"
                  min={1}
                  value={form.height}
                  onChange={(e) => setForm((p) => ({ ...p, height: Number(e.target.value) || 70 }))}
                />
                <Input
                  label="Pööre (°)"
                  type="number"
                  min={0}
                  max={360}
                  value={form.rotationDegree}
                  onChange={(e) => setForm((p) => ({ ...p, rotationDegree: Number(e.target.value) || 0 }))}
                />
              </div>
              <label className="flex items-center gap-2 text-stone-400">
                <input
                  type="checkbox"
                  checked={form.active}
                  onChange={(e) => setForm((p) => ({ ...p, active: e.target.checked }))}
                  className="rounded border-restaurant-border bg-restaurant-card text-restaurant-gold focus:ring-restaurant-gold"
                />
                Aktiivne
              </label>
              <div className="flex gap-2">
                <Button type="submit" disabled={submitting}>
                  {submitting ? 'Salvestan…' : editingId ? 'Salvesta muudatused' : 'Lisa laud'}
                </Button>
                {editingId && (
                  <Button type="button" onClick={cancelEdit} className="bg-stone-600 hover:bg-stone-500">
                    Tühista
                  </Button>
                )}
              </div>
            </form>
          </Card>

          <Card className="lg:col-span-1">
            <h2 className="text-lg font-medium text-stone-200 mb-4">Kõik lauad ({totalElements})</h2>
            {loading ? (
              <p className="text-stone-500">Laen…</p>
            ) : tables.length === 0 ? (
              <p className="text-stone-500">Lauad puuduvad. Lisa esimene laud vasakul.</p>
            ) : (
              <ul className="space-y-2 max-h-[70vh] overflow-y-auto">
                {tables.map((t) => (
                  <li
                    key={t.id}
                    className="flex items-center justify-between gap-2 p-3 rounded-lg bg-restaurant-dark/50 border border-restaurant-border"
                  >
                    <div className="min-w-0">
                      <span className="font-medium text-stone-200">{t.label}</span>
                      <span className="text-stone-500 text-sm ml-2">
                        {t.capacity} kohta, {zoneName(t.zoneId)}
                      </span>
                      {t.featureIds?.length > 0 && (
                        <p className="text-stone-500 text-xs mt-0.5">{featureNames(t.featureIds)}</p>
                      )}
                    </div>
                    <div className="flex shrink-0 gap-1">
                      <button
                        type="button"
                        onClick={() => handleEdit(t.id)}
                        className="px-2 py-1 text-sm text-restaurant-gold hover:underline"
                      >
                        Muuda
                      </button>
                      <button
                        type="button"
                        onClick={() => handleDelete(t.id, t.label)}
                        className="px-2 py-1 text-sm text-amber-400 hover:underline"
                      >
                        Kustuta
                      </button>
                    </div>
                  </li>
                ))}
              </ul>
            )}
          </Card>
        </div>
      </main>
    </div>
  );
}
