// widgets.jsx — Android home-screen widgets for Caderninho de Vendas
// Widgets render at standard Android cell sizes scaled to fit a phone column:
//   2x1 ≈ 168×80 in our 4-col grid
//   4x2 ≈ 350×170
// Each widget has a paper texture, soft shadow, rounded 18px corners (Android 12+ feel),
// and a tiny brand strip with the handwritten "caderninho" mark.

// ─────────────────────────────────────────────────────────────
// Widget chrome — paper card w/ rounded corners + shadow
// ─────────────────────────────────────────────────────────────
function WidgetCard({ children, w, h, style }) {
  return (
    <div className="paper-bg" style={{
      width: w, height: h,
      borderRadius: 22,
      padding: 14,
      boxShadow: '0 2px 4px rgba(0,0,0,0.18), 0 10px 24px rgba(0,0,0,0.18)',
      display: 'flex', flexDirection: 'column',
      fontFamily: 'var(--font-body)', color: 'var(--ink)',
      position: 'relative', overflow: 'hidden',
      ...style,
    }}>{children}</div>
  );
}

// Tiny brand strip — visible at top-right of every widget
function WidgetBrand() {
  return (
    <span style={{
      fontFamily: 'Caveat, cursive', fontSize: 16, color: 'var(--ink-2)',
      lineHeight: 1, opacity: 0.85,
    }}>caderninho</span>
  );
}

// ─────────────────────────────────────────────────────────────
// A1 — "+ Pedido" compact (2x1)
// ─────────────────────────────────────────────────────────────
function WidgetA1({ w = 178, h = 92 }) {
  return (
    <WidgetCard w={w} h={h} style={{ padding: 12, justifyContent: 'space-between' }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
        <div style={{
          width: 30, height: 30, borderRadius: '50%',
          background: 'var(--green)', color: '#fff',
          display: 'flex', alignItems: 'center', justifyContent: 'center',
          boxShadow: '0 2px 6px rgba(63,125,85,0.4)',
        }}>
          <I.plus size={18} stroke={2.4} />
        </div>
        <WidgetBrand />
      </div>
      <div>
        <div style={{ fontSize: 17, fontWeight: 800, lineHeight: 1.1, letterSpacing: -0.2 }}>
          Anotar venda
        </div>
        <div style={{ fontSize: 11.5, color: 'var(--ink-2)', marginTop: 2, fontWeight: 500 }}>
          toque para registrar
        </div>
      </div>
    </WidgetCard>
  );
}

// ─────────────────────────────────────────────────────────────
// A2 — "Pagam hoje" (4x2) — THE HERO WIDGET
// ─────────────────────────────────────────────────────────────
function WidgetA2({ w = 360, h = 188, empty = false }) {
  const items = [
    { name: 'Maria Souza',    value: 45.00 },
    { name: 'Joana Pereira',  value: 22.00 },
    { name: 'Carla Lima',     value: 30.00 },
  ];
  const total = items.reduce((s, i) => s + i.value, 0);
  return (
    <WidgetCard w={w} h={h} style={{ padding: 14, gap: 0 }}>
      {/* Header */}
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'baseline', marginBottom: 8 }}>
        <div style={{ display: 'flex', alignItems: 'baseline', gap: 8 }}>
          <span style={{ fontSize: 16, fontWeight: 800, letterSpacing: -0.2 }}>Pagam hoje</span>
          <span style={{ fontSize: 12, color: 'var(--ink-2)', fontWeight: 600 }}>ter 7/5</span>
        </div>
        <WidgetBrand />
      </div>

      {empty ? (
        <div style={{
          flex: 1, display: 'flex', flexDirection: 'column',
          alignItems: 'center', justifyContent: 'center',
          gap: 4, color: 'var(--ink-2)',
        }}>
          <div style={{ fontSize: 15, fontWeight: 700, color: 'var(--ink)' }}>Ninguém vence hoje.</div>
          <div style={{ fontFamily: 'Caveat, cursive', fontSize: 22, color: 'var(--green)' }}>Bom dia!</div>
        </div>
      ) : (
        <>
          {/* Items list */}
          <div style={{ flex: 1, display: 'flex', flexDirection: 'column' }}>
            {items.map((it, idx) => (
              <div key={it.name} style={{
                display: 'flex', alignItems: 'center', gap: 10,
                padding: '7px 0',
                borderBottom: idx < items.length - 1 ? '1px solid var(--rule)' : 'none',
              }}>
                <span style={{
                  flex: 1, fontSize: 14, fontWeight: 600,
                  whiteSpace: 'nowrap', overflow: 'hidden', textOverflow: 'ellipsis',
                }}>{it.name}</span>
                <Money value={it.value} size={14} weight={800} />
                <button style={{
                  width: 26, height: 26, borderRadius: '50%',
                  background: 'var(--green-soft)', color: 'var(--green)',
                  border: 'none', display: 'flex', alignItems: 'center', justifyContent: 'center',
                  flexShrink: 0,
                }}>
                  <I.check size={15} stroke={2.6} />
                </button>
              </div>
            ))}
          </div>
          {/* Footer */}
          <div style={{
            marginTop: 6, paddingTop: 8,
            borderTop: '1px solid var(--rule-2)',
            display: 'flex', justifyContent: 'space-between', alignItems: 'baseline',
          }}>
            <span style={{ fontSize: 12, color: 'var(--ink-2)', fontWeight: 600 }}>total a receber</span>
            <Money value={total} size={16} weight={800} color="var(--green)" />
          </div>
        </>
      )}
    </WidgetCard>
  );
}

// ─────────────────────────────────────────────────────────────
// A3 — "Em aberto" (4x2)
// ─────────────────────────────────────────────────────────────
function WidgetA3({ w = 360, h = 188, empty = false }) {
  const items = [
    { name: 'Bia Almeida',  value: 60.00, status: 'atrasado 2 dias', tone: 'red' },
    { name: 'Maria Souza',  value: 45.00, status: 'vence hoje',      tone: 'amber' },
    { name: 'Ana Ribeiro',  value: 80.00, status: 'vence sex',       tone: 'default' },
    { name: 'Carla Lima',   value: 30.00, status: 'vence 14/5',      tone: 'default' },
  ];
  const total = items.reduce((s, i) => s + i.value, 0);
  return (
    <WidgetCard w={w} h={h} style={{ padding: 14 }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'baseline', marginBottom: 6 }}>
        <div style={{ display: 'flex', alignItems: 'baseline', gap: 8 }}>
          <span style={{ fontSize: 16, fontWeight: 800, letterSpacing: -0.2 }}>Em aberto</span>
          <Money value={total} size={13} weight={700} color="var(--ink-2)" />
        </div>
        <WidgetBrand />
      </div>
      {empty ? (
        <div style={{ flex: 1, display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', gap: 4 }}>
          <div style={{ fontSize: 15, fontWeight: 700 }}>Tudo em dia.</div>
          <div style={{ fontFamily: 'Caveat, cursive', fontSize: 22, color: 'var(--green)' }}>Aproveita!</div>
        </div>
      ) : (
        <div style={{ flex: 1, display: 'flex', flexDirection: 'column' }}>
          {items.map((it, idx) => (
            <div key={it.name} style={{
              display: 'flex', alignItems: 'center', gap: 8,
              padding: '5px 0',
              borderBottom: idx < items.length - 1 ? '1px solid var(--rule)' : 'none',
            }}>
              <span className={`dot ${it.tone === 'red' ? 'dot-red' : it.tone === 'amber' ? 'dot-amber' : ''}`}
                style={{ background: it.tone === 'default' ? 'var(--rule-2)' : undefined, width: 6, height: 6 }} />
              <span style={{ flex: 1, fontSize: 13.5, fontWeight: 600, whiteSpace: 'nowrap', overflow: 'hidden', textOverflow: 'ellipsis' }}>
                {it.name}
              </span>
              <span style={{
                fontSize: 11, color: it.tone === 'red' ? 'var(--red)' : 'var(--ink-2)',
                fontWeight: 700, marginRight: 4,
              }}>{it.status}</span>
              <Money value={it.value} size={13.5} weight={800} />
            </div>
          ))}
        </div>
      )}
    </WidgetCard>
  );
}

Object.assign(window, { WidgetCard, WidgetA1, WidgetA2, WidgetA3 });
