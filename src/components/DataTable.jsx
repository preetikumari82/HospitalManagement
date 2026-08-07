import { useMemo, useState } from "react";

/**
 * Generic client-side DataTable: search, column sorting, per-column select
 * filters, and pagination. The Spring endpoints in this project return
 * unpaginated lists, so this component does the pagination/sorting/filtering
 * work in the browser once the data has been fetched.
 *
 * columns: [{ key, label, sortable=true, accessor?: row => value, render?: (row)=>node, width? }]
 * filters: [{ key, label, options: [{value,label}], accessor?: row => value }]
 */
export default function DataTable({
  columns,
  data,
  searchKeys = [],
  searchPlaceholder = "Search...",
  filters = [],
  pageSizeOptions = [5, 8, 10, 25, 50],
  defaultPageSize = 8,
  emptyMessage = "No records found.",
  rowKey = (row) => row.id,
  toolbarExtra = null,
}) {
  const [search, setSearch] = useState("");
  const [activeFilters, setActiveFilters] = useState({});
  const [sort, setSort] = useState({ key: null, dir: "asc" });
  const [page, setPage] = useState(1);
  const [pageSize, setPageSize] = useState(defaultPageSize);

  const filtered = useMemo(() => {
    let rows = data || [];

    if (search.trim()) {
      const q = search.trim().toLowerCase();
      rows = rows.filter((row) =>
        searchKeys.some((k) => {
          const val = typeof k === "function" ? k(row) : row[k];
          return String(val ?? "").toLowerCase().includes(q);
        })
      );
    }

    for (const f of filters) {
      const val = activeFilters[f.key];
      if (val) {
        rows = rows.filter((row) => {
          const rowVal = f.accessor ? f.accessor(row) : row[f.key];
          return String(rowVal ?? "") === val;
        });
      }
    }

    return rows;
  }, [data, search, searchKeys, filters, activeFilters]);

  const sorted = useMemo(() => {
    if (!sort.key) return filtered;
    const col = columns.find((c) => c.key === sort.key);
    const accessor = col?.accessor || ((row) => row[sort.key]);
    const copy = [...filtered];
    copy.sort((a, b) => {
      const av = accessor(a);
      const bv = accessor(b);
      if (av == null && bv == null) return 0;
      if (av == null) return 1;
      if (bv == null) return -1;
      if (typeof av === "number" && typeof bv === "number") return av - bv;
      return String(av).localeCompare(String(bv), undefined, { numeric: true, sensitivity: "base" });
    });
    if (sort.dir === "desc") copy.reverse();
    return copy;
  }, [filtered, sort, columns]);

  const totalPages = Math.max(1, Math.ceil(sorted.length / pageSize));
  const safePage = Math.min(page, totalPages);
  const paged = useMemo(
    () => sorted.slice((safePage - 1) * pageSize, safePage * pageSize),
    [sorted, safePage, pageSize]
  );

  const toggleSort = (key, sortable) => {
    if (sortable === false) return;
    setSort((s) => {
      if (s.key !== key) return { key, dir: "asc" };
      if (s.dir === "asc") return { key, dir: "desc" };
      return { key: null, dir: "asc" };
    });
  };

  return (
    <div>
      {/* Toolbar: search + filters */}
      <div className="flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between mb-4">
        <div className="flex flex-1 flex-wrap gap-2">
          {searchKeys.length > 0 && (
            <div className="relative w-full sm:w-64">
              <svg className="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-ink-300" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M21 21l-4.35-4.35M17 10a7 7 0 11-14 0 7 7 0 0114 0z" />
              </svg>
              <input
                value={search}
                onChange={(e) => {
                  setSearch(e.target.value);
                  setPage(1);
                }}
                placeholder={searchPlaceholder}
                className="input pl-9"
              />
            </div>
          )}
          {filters.map((f) => (
            <select
              key={f.key}
              value={activeFilters[f.key] || ""}
              onChange={(e) => {
                setActiveFilters((s) => ({ ...s, [f.key]: e.target.value }));
                setPage(1);
              }}
              className="input w-auto min-w-[9rem]"
            >
              <option value="">{f.label}: All</option>
              {f.options.map((o) => (
                <option key={o.value} value={o.value}>
                  {o.label}
                </option>
              ))}
            </select>
          ))}
          {(search || Object.values(activeFilters).some(Boolean)) && (
            <button
              className="btn-ghost text-xs"
              onClick={() => {
                setSearch("");
                setActiveFilters({});
                setPage(1);
              }}
            >
              Clear
            </button>
          )}
        </div>
        {toolbarExtra}
      </div>

      {/* Table */}
      <div className="card overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full border-collapse">
            <thead>
              <tr className="border-b border-line bg-ink-50/60">
                {columns.map((col) => (
                  <th
                    key={col.key}
                    className={`th ${col.sortable !== false ? "cursor-pointer hover:text-ink-800" : ""}`}
                    style={col.width ? { width: col.width } : undefined}
                    onClick={() => toggleSort(col.key, col.sortable)}
                  >
                    <span className="inline-flex items-center gap-1">
                      {col.label}
                      {col.sortable !== false && (
                        <span className="text-ink-300">
                          {sort.key === col.key ? (sort.dir === "asc" ? "↑" : "↓") : "↕"}
                        </span>
                      )}
                    </span>
                  </th>
                ))}
              </tr>
            </thead>
            <tbody>
              {paged.length === 0 && (
                <tr>
                  <td colSpan={columns.length} className="td text-center py-10 text-ink-400">
                    {emptyMessage}
                  </td>
                </tr>
              )}
              {paged.map((row) => (
                <tr key={rowKey(row)} className="border-b border-line/70 last:border-0 hover:bg-clover-50/40 transition-colors">
                  {columns.map((col) => (
                    <td key={col.key} className="td">
                      {col.render ? col.render(row) : String((col.accessor ? col.accessor(row) : row[col.key]) ?? "—")}
                    </td>
                  ))}
                </tr>
              ))}
            </tbody>
          </table>
        </div>

        {/* Pagination footer */}
        <div className="flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between border-t border-line px-4 py-3 bg-ink-50/40 text-sm">
          <div className="flex items-center gap-2 text-ink-500">
            <span>
              Showing {sorted.length === 0 ? 0 : (safePage - 1) * pageSize + 1}–
              {Math.min(safePage * pageSize, sorted.length)} of {sorted.length}
            </span>
            <select
              value={pageSize}
              onChange={(e) => {
                setPageSize(Number(e.target.value));
                setPage(1);
              }}
              className="rounded-md border border-line bg-white px-2 py-1 text-xs"
            >
              {pageSizeOptions.map((n) => (
                <option key={n} value={n}>
                  {n} / page
                </option>
              ))}
            </select>
          </div>
          <div className="flex items-center gap-1">
            <button className="btn-outline px-2.5 py-1 text-xs" disabled={safePage <= 1} onClick={() => setPage(1)}>
              «
            </button>
            <button className="btn-outline px-2.5 py-1 text-xs" disabled={safePage <= 1} onClick={() => setPage((p) => p - 1)}>
              ‹ Prev
            </button>
            <span className="px-2 text-xs text-ink-500">
              Page {safePage} of {totalPages}
            </span>
            <button
              className="btn-outline px-2.5 py-1 text-xs"
              disabled={safePage >= totalPages}
              onClick={() => setPage((p) => p + 1)}
            >
              Next ›
            </button>
            <button
              className="btn-outline px-2.5 py-1 text-xs"
              disabled={safePage >= totalPages}
              onClick={() => setPage(totalPages)}
            >
              »
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}
