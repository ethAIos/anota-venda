// screens-b.jsx — Bloco B: app screens (B1–B5)
// All screens share PhoneShell paper bg + a thin app header with brand mark.
// Headers are minimal — close (×) on the left or back arrow + screen title.

// ─────────────────────────────────────────────────────────────
// Reusable: a thin top bar with optional close/back + title + right slot
// ─────────────────────────────────────────────────────────────
function ScreenTopBar({ leading = 'close', title, onLeading, right }) {
  return (
    <div style={{
      display: 'flex', alignItems: 'center', gap: 8,
      padding: '6px 12px 6px 8px', minHeight: 52,
      flexShrink: 0,
    }}>
      <button onClick={onLeading} style={{
        width: 40, height: 40, borderRadius: 20,
        background: 'transparent', border: 'none', color: 'var(--ink)',
        display: 'flex', alignItems: 'center', justifyContent: 'center',
        cursor: 'pointer',
      }}>
        {leading === 'close' ? <I.close size={22} /> : <I.arrowL size={22} />}
      </button>
      <span style={{ flex: 1, fontSize: 17, fontWeight: 800, letterSpacing: -0.2 }}>{title}</span>
      {right}
    </div>
  );
}

// Section label — small uppercase eyebrow
function FieldLabel({ children }) {
  return (
    <div style={{
      fontSize: 12, fontWeight: 700, letterSpacing: 0.6,
      color: 'var(--ink-2)', textTransform: 'uppercase', marginBottom: 6,
    }}>{children}</div>
  );
}

// Underlined paper input — looks like writing on a notebook line
function PaperInput({ value, placeholder, suggestion, big = false, prefix }) {
  return (
    <div style={{ position: 'relative' }}>
      <div style={{
        display: 'flex', alignItems: 'baseline', gap: 6,
        borderBottom: '1.5px solid var(--rule-2)',
        paddingBottom: 6,
      }}>
        {prefix && <span style={{ fontSize: big ? 28 : 17, color: 'var(--ink-2)', fontWeight: 700 }}>{prefix}</span>}
        <span style={{
          fontSize: big ? 32 : 18,
          fontWeight: big ? 800 : 600,
          color: value ? 'var(--ink)' : 'var(--ink-2)',
          flex: 1,
          letterSpacing: big ? -1 : -0.1,
        }}>{value || placeholder}</span>
      </div>
      {suggestion && !value && (
        <div style={{ marginTop: 8 }}>
          <span style={{
            fontSize: 13, color: 'var(--ink-2)', fontWeight: 600,
            background: 'var(--paper-2)', padding: '4px 10px', borderRadius: 99,
            border: '1px solid var(--rule)',
          }}>{suggestion}</span>
        </div>
      )}
    </div>
  );
}

// Suggestion chip row
function ChipRow({ chips, selected }) {
  return (
    <div style={{ display: 'flex', gap: 6, flexWrap: 'wrap' }}>
      {chips.map((c) => (
        <span key={c} style={{
          fontSize: 13, fontWeight: 700,
          padding: '6px 12px', borderRadius: 99,
          background: c === selected ? 'var(--ink)' : 'var(--paper-2)',
          color: c === selected ? 'var(--paper)' : 'var(--ink)',
          border: c === selected ? 'none' : '1px solid var(--rule)',
        }}>{c}</span>
      ))}
    </div>
  );
}

// Toggle pair — "à vista" / "fiado" big buttons
function BigToggle({ options, selected }) {
  return (
    <div style={{
      display: 'grid', gridTemplateColumns: `repeat(${options.length}, 1fr)`,
      gap: 8,
    }}>
      {options.map((o) => {
        const sel = o.value === selected;
        return (
          <div key={o.value} style={{
            padding: '14px 12px', borderRadius: 12,
            background: sel ? 'var(--ink)' : 'var(--paper-2)',
            color: sel ? 'var(--paper)' : 'var(--ink)',
            border: sel ? 'none' : '1px solid var(--rule)',
            display: 'flex', flexDirection: 'column', alignItems: 'center', gap: 2,
          }}>
            <span style={{ fontSize: 16, fontWeight: 800 }}>{o.label}</span>
            {o.sub && <span style={{ fontSize: 11.5, opacity: 0.7, fontWeight: 600 }}>{o.sub}</span>}
          </div>
        );
      })}
    </div>
  );
}

// Numeric stepper — 1 .. 6
function Stepper({ value, max = 6, min = 1 }) {
  return (
    <div style={{ display: 'flex', alignItems: 'center', gap: 6 }}>
      {Array.from({ length: max }, (_, i) => i + 1).map((n) => {
        const sel = n === value;
        return (
          <span key={n} style={{
            width: 36, height: 36, borderRadius: 10,
            display: 'flex', alignItems: 'center', justifyContent: 'center',
            fontWeight: 800, fontSize: 15,
            background: sel ? 'var(--ink)' : 'var(--paper-2)',
            color: sel ? 'var(--paper)' : 'var(--ink)',
            border: sel ? 'none' : '1px solid var(--rule)',
          }}>{n}</span>
        );
      })}
    </div>
  );
}

// ─────────────────────────────────────────────────────────────
// B1 — Anotar venda
// ─────────────────────────────────────────────────────────────
function ScreenB1({ confirmation = false }) {
  return (
    <PhoneShell>
      <ScreenTopBar title="Nova venda" leading="close" />
      {confirmation ? (
        <div style={{
          flex: 1, display: 'flex', flexDirection: 'column',
          alignItems: 'center', justifyContent: 'center', padding: '24px 32px',
          gap: 18, textAlign: 'center',
        }}>
          <div style={{
            width: 72, height: 72, borderRadius: '50%',
            background: 'var(--green-soft)', color: 'var(--green)',
            display: 'flex', alignItems: 'center', justifyContent: 'center',
          }}>
            <I.check size={36} stroke={2.6} />
          </div>
          <div>
            <div style={{ fontFamily: 'Caveat, cursive', fontSize: 38, color: 'var(--green)', lineHeight: 1 }}>Anotado!</div>
            <div style={{ fontSize: 16, color: 'var(--ink)', marginTop: 12, lineHeight: 1.45, fontWeight: 600 }}>
              Maria deve <Money value={45} size={16} weight={800} color="var(--ink)" /> até 12/05.
            </div>
            <div style={{ fontSize: 13, color: 'var(--ink-2)', marginTop: 6, fontWeight: 600 }}>
              avisamos no widget no dia.
            </div>
          </div>
          <div style={{ display: 'flex', gap: 8, marginTop: 8 }}>
            <GhostButton color="paper" size="md">Anotar outra</GhostButton>
            <PrimaryButton full={false}>Ver Maria</PrimaryButton>
          </div>
        </div>
      ) : (
        <div style={{ flex: 1, padding: '8px 22px 16px', display: 'flex', flexDirection: 'column', gap: 22, overflow: 'hidden' }}>
          {/* Cliente */}
          <div>
            <FieldLabel>Cliente</FieldLabel>
            <PaperInput value="Maria" placeholder="Nome do cliente" />
            <div style={{ marginTop: 10 }}>
              <ChipRow chips={['Maria Souza', 'Maria Helena', 'Marina Dias']} selected="Maria Souza" />
            </div>
          </div>

          {/* O quê */}
          <div>
            <FieldLabel>O quê</FieldLabel>
            <PaperInput value="Batom Avon vermelho" placeholder="ex: Batom, perfume, panela…" />
          </div>

          {/* Valor */}
          <div>
            <FieldLabel>Valor</FieldLabel>
            <PaperInput value="45,00" placeholder="0,00" big prefix="R$" />
          </div>

          {/* À vista / fiado */}
          <div>
            <FieldLabel>Como vai pagar?</FieldLabel>
            <BigToggle
              selected="fiado"
              options={[
                { value: 'avista', label: 'À vista', sub: 'recebido hoje' },
                { value: 'fiado', label: 'Fiado', sub: 'me paga depois' },
              ]}
            />
          </div>

          {/* Fiado fields */}
          <div style={{
            background: 'var(--paper-2)', borderRadius: 14,
            padding: '14px 14px 16px', border: '1px solid var(--rule)',
            display: 'flex', flexDirection: 'column', gap: 14,
          }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
              <I.cal size={18} />
              <span style={{ fontSize: 14, fontWeight: 700 }}>Quando paga?</span>
              <span style={{ marginLeft: 'auto', fontSize: 15, fontWeight: 800 }}>12/05</span>
            </div>
            <div>
              <span style={{ fontSize: 13, fontWeight: 700, color: 'var(--ink-2)' }}>Em quantas vezes?</span>
              <div style={{ marginTop: 8 }}><Stepper value={1} /></div>
            </div>
          </div>

          <div style={{ flex: 1 }} />
          <PrimaryButton>Anotar</PrimaryButton>
        </div>
      )}
    </PhoneShell>
  );
}

// ─────────────────────────────────────────────────────────────
// B2 — Detalhes do cliente
// ─────────────────────────────────────────────────────────────
function ScreenB2() {
  const orders = [
    { id: 1, what: 'Batom Avon vermelho', date: '7/5',  total: 45, due: 'vence 12/5', tone: 'amber' },
    { id: 2, what: 'Perfume Natura',      date: '20/4', total: 90, due: 'pago em 25/4', tone: 'green' },
    { id: 3, what: 'Salgado para festa',  date: '15/4', total: 60, due: 'atrasado 22 dias', tone: 'red' },
  ];
  const balance = 105;
  return (
    <PhoneShell>
      <ScreenTopBar title="Maria Souza" leading="back" right={
        <button style={{
          width: 40, height: 40, borderRadius: 20, background: 'transparent', border: 'none', color: 'var(--ink)',
          display: 'flex', alignItems: 'center', justifyContent: 'center',
        }}><I.more size={22} /></button>
      } />

      <div style={{ flex: 1, overflow: 'auto', padding: '0 22px 16px', display: 'flex', flexDirection: 'column', gap: 18 }}>
        {/* Header card */}
        <div style={{
          background: 'var(--paper-2)', border: '1px solid var(--rule)',
          borderRadius: 16, padding: 18, display: 'flex', alignItems: 'center', gap: 14,
        }}>
          <Avatar name="Maria" size={52} tone="amber" />
          <div style={{ flex: 1, minWidth: 0 }}>
            <div style={{ fontSize: 17, fontWeight: 800 }}>Maria Souza</div>
            <div style={{ display: 'flex', alignItems: 'center', gap: 6, color: 'var(--ink-2)', fontSize: 13, fontWeight: 600, marginTop: 2 }}>
              <I.phone size={13} stroke={2} /> (11) 98765-4321
            </div>
          </div>
        </div>

        {/* Saldo em destaque */}
        <div style={{ textAlign: 'center', padding: '4px 0 8px' }}>
          <div style={{ fontSize: 12, fontWeight: 700, letterSpacing: 0.6, color: 'var(--ink-2)', textTransform: 'uppercase' }}>
            saldo em aberto
          </div>
          <div style={{ marginTop: 4 }}>
            <Money value={balance} size={42} weight={800} color="var(--red)" />
          </div>
          <div style={{ fontSize: 13, color: 'var(--ink-2)', marginTop: 2, fontWeight: 600 }}>
            de 2 pedidos · 1 atrasado
          </div>
        </div>

        {/* Ações */}
        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 8 }}>
          <PrimaryButton icon={<I.plus size={17} stroke={2.6} />}>Novo pedido</PrimaryButton>
          <PrimaryButton icon={<I.check size={17} stroke={2.6} />}>Recebi</PrimaryButton>
        </div>
        <GhostButton color="paper" icon={<I.whatsapp size={17} />}>Mandar cobrança no WhatsApp</GhostButton>

        {/* Pedidos */}
        <div>
          <FieldLabel>Pedidos</FieldLabel>
          <div style={{
            background: 'var(--paper)',
            border: '1px solid var(--rule)', borderRadius: 14, overflow: 'hidden',
          }}>
            {orders.map((o, i) => (
              <div key={o.id} style={{
                display: 'flex', alignItems: 'center', gap: 12,
                padding: '14px 14px',
                borderBottom: i < orders.length - 1 ? '1px solid var(--rule)' : 'none',
              }}>
                <span className={`dot ${o.tone === 'red' ? 'dot-red' : o.tone === 'green' ? 'dot-green' : 'dot-amber'}`} />
                <div style={{ flex: 1, minWidth: 0 }}>
                  <div style={{ fontSize: 14.5, fontWeight: 700, whiteSpace: 'nowrap', overflow: 'hidden', textOverflow: 'ellipsis' }}>
                    {o.what}
                  </div>
                  <div style={{ fontSize: 12, color: o.tone === 'red' ? 'var(--red)' : 'var(--ink-2)', fontWeight: 600, marginTop: 1 }}>
                    {o.date} · {o.due}
                  </div>
                </div>
                <Money value={o.total} size={15} weight={800} color={o.tone === 'green' ? 'var(--ink-2)' : 'var(--ink)'} />
              </div>
            ))}
          </div>
        </div>
      </div>
    </PhoneShell>
  );
}

// ─────────────────────────────────────────────────────────────
// B3 — Detalhes do pedido
// ─────────────────────────────────────────────────────────────
function ScreenB3() {
  const parts = [
    { n: 1, due: '15/4', value: 30, status: 'recebida', tone: 'green' },
    { n: 2, due: '15/5', value: 30, status: 'em aberto', tone: 'default' },
    { n: 3, due: '15/6', value: 30, status: 'em aberto', tone: 'default' },
  ];
  const total = parts.reduce((s, p) => s + p.value, 0);
  const left  = parts.filter(p => p.tone !== 'green').reduce((s, p) => s + p.value, 0);
  return (
    <PhoneShell>
      <ScreenTopBar title="Pedido" leading="back" right={
        <button style={{
          width: 40, height: 40, borderRadius: 20, background: 'transparent', border: 'none', color: 'var(--ink)',
          display: 'flex', alignItems: 'center', justifyContent: 'center',
        }}><I.more size={22} /></button>
      } />
      <div style={{ flex: 1, overflow: 'auto', padding: '0 22px 16px', display: 'flex', flexDirection: 'column', gap: 18 }}>
        {/* Heading */}
        <div>
          <div style={{ fontSize: 13, color: 'var(--ink-2)', fontWeight: 700 }}>Joana Pereira</div>
          <div style={{ fontSize: 22, fontWeight: 800, letterSpacing: -0.4, marginTop: 2 }}>
            Panela Tupperware
          </div>
          <div style={{ fontSize: 13, color: 'var(--ink-2)', fontWeight: 600, marginTop: 4 }}>
            anotado em 15/4 · 3x de R$ 30,00
          </div>
        </div>

        {/* Total / restante */}
        <div style={{
          background: 'var(--paper-2)', border: '1px solid var(--rule)',
          borderRadius: 14, padding: '14px 16px',
          display: 'flex', justifyContent: 'space-between', alignItems: 'center',
        }}>
          <div>
            <div style={{ fontSize: 12, fontWeight: 700, color: 'var(--ink-2)', textTransform: 'uppercase', letterSpacing: 0.6 }}>total</div>
            <Money value={total} size={20} weight={800} />
          </div>
          <div style={{ width: 1, height: 32, background: 'var(--rule-2)' }} />
          <div>
            <div style={{ fontSize: 12, fontWeight: 700, color: 'var(--ink-2)', textTransform: 'uppercase', letterSpacing: 0.6 }}>falta</div>
            <Money value={left} size={20} weight={800} color="var(--red)" />
          </div>
        </div>

        {/* Parcelas */}
        <div>
          <FieldLabel>Parcelas</FieldLabel>
          <div style={{ display: 'flex', flexDirection: 'column', gap: 8 }}>
            {parts.map((p) => {
              const paid = p.tone === 'green';
              return (
                <div key={p.n} style={{
                  background: 'var(--paper)',
                  border: '1px solid var(--rule)',
                  borderRadius: 12, padding: '12px 14px',
                  display: 'flex', alignItems: 'center', gap: 12,
                  opacity: paid ? 0.7 : 1,
                }}>
                  <div style={{
                    width: 32, height: 32, borderRadius: 8,
                    background: paid ? 'var(--green-soft)' : 'var(--paper-2)',
                    color: paid ? 'var(--green)' : 'var(--ink-2)',
                    display: 'flex', alignItems: 'center', justifyContent: 'center',
                    fontWeight: 800, fontSize: 13,
                    border: paid ? 'none' : '1px solid var(--rule)',
                  }}>
                    {paid ? <I.check size={17} stroke={2.6} /> : p.n}
                  </div>
                  <div style={{ flex: 1, minWidth: 0 }}>
                    <div style={{ fontSize: 14.5, fontWeight: 700, textDecoration: paid ? 'line-through' : 'none' }}>
                      Parcela {p.n}/3
                    </div>
                    <div style={{ fontSize: 12, color: 'var(--ink-2)', fontWeight: 600, marginTop: 1 }}>
                      vence {p.due} · {p.status}
                    </div>
                  </div>
                  <Money value={p.value} size={15} weight={800} color={paid ? 'var(--ink-2)' : 'var(--ink)'} />
                  {!paid && (
                    <button style={{
                      background: 'var(--green-soft)', color: 'var(--green)',
                      border: 'none', borderRadius: 8, padding: '6px 10px',
                      fontSize: 13, fontWeight: 800, fontFamily: 'inherit',
                    }}>Recebi</button>
                  )}
                </div>
              );
            })}
          </div>
        </div>

        {/* Observação */}
        <div>
          <FieldLabel>Observação</FieldLabel>
          <div style={{
            background: 'var(--paper)', border: '1px dashed var(--rule-2)',
            borderRadius: 12, padding: '12px 14px',
            fontSize: 14, color: 'var(--ink)', fontWeight: 500, lineHeight: 1.5,
            fontStyle: 'italic',
          }}>
            "entregar na casa dela, perto do mercado da Vila"
          </div>
        </div>

        <GhostButton color="paper" icon={<I.pencil size={16} />}>Editar pedido</GhostButton>
      </div>
    </PhoneShell>
  );
}

// ─────────────────────────────────────────────────────────────
// WeekPicker — inline horizontal week strip with totals/dots per day
// Compact paper card; selected day = ink filled, today = green ring.
// Each cell shows weekday abbrev, date number, and a tiny indicator
// (R$ amount when there's activity, or — when empty).
// ─────────────────────────────────────────────────────────────
const _wd = ['seg', 'ter', 'qua', 'qui', 'sex', 'sáb', 'dom'];
function WeekPicker({ week, selected, todayIdx = 1 }) {
  return (
    <div style={{
      background: 'var(--paper-2)', border: '1px solid var(--rule)',
      borderRadius: 14, padding: '10px 6px 8px',
    }}>
      {/* Month + arrows */}
      <div style={{
        display: 'flex', alignItems: 'center', justifyContent: 'space-between',
        padding: '0 8px 8px',
      }}>
        <button style={{
          width: 26, height: 26, borderRadius: 13,
          background: 'transparent', border: 'none', color: 'var(--ink-2)',
          display: 'flex', alignItems: 'center', justifyContent: 'center', cursor: 'pointer',
        }}><I.chevL size={16} stroke={2.2} /></button>
        <span style={{ fontSize: 13, fontWeight: 800, letterSpacing: 0.2 }}>maio · 2026</span>
        <button style={{
          width: 26, height: 26, borderRadius: 13,
          background: 'transparent', border: 'none', color: 'var(--ink-2)',
          display: 'flex', alignItems: 'center', justifyContent: 'center', cursor: 'pointer',
        }}><I.chevR size={16} stroke={2.2} /></button>
      </div>
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(7, 1fr)', gap: 2 }}>
        {week.map((d, i) => {
          const sel = i === selected;
          const today = i === todayIdx;
          const overdue = d.tone === 'red';
          return (
            <div key={i} style={{
              display: 'flex', flexDirection: 'column', alignItems: 'center', gap: 3,
              padding: '6px 2px 6px',
              background: sel ? 'var(--ink)' : 'transparent',
              color: sel ? 'var(--paper)' : 'var(--ink)',
              borderRadius: 10,
              border: today && !sel ? '1.5px solid var(--green)' : '1.5px solid transparent',
              minWidth: 0,
            }}>
              <span style={{
                fontSize: 10, fontWeight: 700, letterSpacing: 0.3, textTransform: 'uppercase',
                color: sel ? 'rgba(250,246,239,0.7)' : 'var(--ink-2)',
              }}>{_wd[i]}</span>
              <span style={{ fontSize: 17, fontWeight: 800, lineHeight: 1, letterSpacing: -0.3 }}>{d.date}</span>
              <span style={{
                fontSize: 9.5, fontWeight: 800,
                color: sel
                  ? (overdue ? '#F3DED5' : 'rgba(250,246,239,0.85)')
                  : (overdue ? 'var(--red)' : (d.value ? 'var(--green)' : 'var(--ink-2)')),
                letterSpacing: 0.1,
              }}>
                {d.value ? `R$${d.value}` : '—'}
              </span>
            </div>
          );
        })}
      </div>
    </div>
  );
}

// Tappable date pill — looks like a chip that opens the picker
function DatePill({ label, open, total, count }) {
  return (
    <button style={{
      width: '100%', textAlign: 'left',
      background: open ? 'var(--paper)' : 'var(--paper-2)',
      border: `1px solid ${open ? 'var(--ink)' : 'var(--rule)'}`,
      borderRadius: 12, padding: '10px 14px',
      display: 'flex', alignItems: 'center', gap: 10,
      cursor: 'pointer', fontFamily: 'inherit',
      transition: 'all 0.15s',
    }}>
      <I.cal size={18} style={{ color: 'var(--ink)' }} />
      <div style={{ flex: 1, minWidth: 0 }}>
        <div style={{ display: 'flex', alignItems: 'baseline', gap: 6 }}>
          <span style={{ fontSize: 16, fontWeight: 800, letterSpacing: -0.2, textTransform: 'capitalize' }}>{label}</span>
        </div>
        <div style={{ fontSize: 12, color: 'var(--ink-2)', fontWeight: 600, marginTop: 1 }}>
          {count != null ? `${count} ${count === 1 ? 'pessoa' : 'pessoas'}` : 'sem cobranças'}
          {total != null && total > 0 && <> · <Money value={total} size={12} weight={700} color="var(--green)" /></>}
        </div>
      </div>
      <span style={{
        display: 'inline-flex', transform: open ? 'rotate(180deg)' : 'none',
        transition: 'transform 0.18s', color: 'var(--ink-2)',
      }}>
        <I.chevD size={18} stroke={2.2} />
      </span>
    </button>
  );
}

// Demo week — value = R$ subtotal that day, tone='red' for atrasado
const DEMO_WEEK = [
  { date: 5, value: 0,  tone: 'default' }, // seg
  { date: 6, value: 60, tone: 'red'     }, // ter — atrasado (ontem)
  { date: 7, value: 97, tone: 'default' }, // qua — HOJE (não, ajusta abaixo)
  { date: 8, value: 0,  tone: 'default' }, // qui
  { date: 9, value: 30, tone: 'default' }, // sex
  { date:10, value: 0,  tone: 'default' }, // sáb
  { date:11, value: 22, tone: 'default' }, // dom
];

// ─────────────────────────────────────────────────────────────
// B4 — Pagam hoje (lista expandida) com seletor de dia
// pickerOpen: bool — calendário semanal expandido?
// selectedDay: 'today' | 'overdue' | 'empty' — qual dia ver
// ─────────────────────────────────────────────────────────────
function ScreenB4({ pickerOpen = false, selectedDay = 'today' }) {
  const days = {
    today: {
      label: 'hoje, 7 de maio',
      idx: 2, // wednesday in our DEMO_WEEK
      items: [
        { name: 'Maria Souza',    what: 'Batom Avon vermelho', value: 45, tone: 'amber' },
        { name: 'Joana Pereira',  what: 'Panela Tupperware',   value: 30, tone: 'amber' },
        { name: 'Carla Lima',     what: 'Blusa estampada',     value: 22, tone: 'amber' },
      ],
      heading: null,
    },
    overdue: {
      label: 'ontem, 6 de maio',
      idx: 1,
      items: [
        { name: 'Bia Almeida', what: 'Kit perfumes Natura', value: 60, tone: 'red' },
      ],
      heading: { tone: 'red', text: 'atrasado · 1 dia' },
    },
    empty: {
      label: 'qui, 8 de maio',
      idx: 3,
      items: [],
      heading: null,
    },
  };
  const d = days[selectedDay];
  const total = d.items.reduce((s, i) => s + i.value, 0);
  const todayIdx = 2; // hardcoded "today" position in week strip

  return (
    <PhoneShell>
      <ScreenTopBar title="Cobranças" leading="back" right={
        <button style={{
          width: 40, height: 40, borderRadius: 20, background: 'transparent', border: 'none', color: 'var(--ink)',
          display: 'flex', alignItems: 'center', justifyContent: 'center',
        }}><I.search size={20} /></button>
      } />

      {/* Date pill + picker (collapsible) */}
      <div style={{ padding: '0 16px 10px', display: 'flex', flexDirection: 'column', gap: 8 }}>
        <DatePill
          label={d.label}
          open={pickerOpen}
          total={total}
          count={d.items.length}
        />
        {pickerOpen && (
          <WeekPicker week={DEMO_WEEK} selected={d.idx} todayIdx={todayIdx} />
        )}
      </div>

      {/* List */}
      <div style={{ flex: 1, overflow: 'auto', padding: '6px 16px 16px', display: 'flex', flexDirection: 'column', gap: 10 }}>
        {d.heading && (
          <div style={{
            display: 'flex', alignItems: 'center', gap: 8,
            padding: '6px 12px',
            background: 'var(--red-soft)',
            borderRadius: 10,
          }}>
            <span className="dot dot-red" />
            <span style={{ fontSize: 13, fontWeight: 800, color: 'var(--red)', letterSpacing: 0.1 }}>{d.heading.text}</span>
          </div>
        )}

        {d.items.length === 0 ? (
          <div style={{
            flex: 1, display: 'flex', flexDirection: 'column',
            alignItems: 'center', justifyContent: 'center', textAlign: 'center',
            gap: 8, padding: 24,
          }}>
            <div style={{
              width: 56, height: 56, borderRadius: '50%',
              background: 'var(--paper-2)', border: '1px solid var(--rule)',
              color: 'var(--ink-2)',
              display: 'flex', alignItems: 'center', justifyContent: 'center',
            }}><I.cal size={26} /></div>
            <div style={{ fontFamily: 'Caveat, cursive', fontSize: 30, color: 'var(--ink)', lineHeight: 1, marginTop: 4 }}>
              dia tranquilo!
            </div>
            <div style={{ fontSize: 14, color: 'var(--ink-2)', fontWeight: 600, lineHeight: 1.4, maxWidth: 240 }}>
              ninguém tem que pagar nesse dia.
            </div>
          </div>
        ) : d.items.map((it) => {
          const isOverdue = it.tone === 'red';
          return (
            <div key={it.name} style={{
              background: 'var(--paper)',
              border: `1px solid ${isOverdue ? '#E8C7BA' : 'var(--rule)'}`,
              borderRadius: 14, padding: 14,
              display: 'flex', flexDirection: 'column', gap: 12,
              boxShadow: 'var(--shadow-paper)',
            }}>
              <div style={{ display: 'flex', alignItems: 'center', gap: 12 }}>
                <Avatar name={it.name} size={40} tone={isOverdue ? 'red' : 'amber'} />
                <div style={{ flex: 1, minWidth: 0 }}>
                  <div style={{ fontSize: 15.5, fontWeight: 800, lineHeight: 1.1 }}>{it.name}</div>
                  <div style={{ fontSize: 12.5, color: 'var(--ink-2)', fontWeight: 600, marginTop: 2,
                    whiteSpace: 'nowrap', overflow: 'hidden', textOverflow: 'ellipsis' }}>{it.what}</div>
                </div>
                <Money value={it.value} size={17} weight={800} color={isOverdue ? 'var(--red)' : 'var(--ink)'} />
              </div>
              <div style={{ display: 'grid', gridTemplateColumns: '1.4fr 1fr 1fr', gap: 6 }}>
                <button style={{
                  background: 'var(--green)', color: '#fff',
                  border: 'none', borderRadius: 10, padding: '10px 8px',
                  fontSize: 14, fontWeight: 800, fontFamily: 'inherit',
                  display: 'flex', alignItems: 'center', justifyContent: 'center', gap: 5,
                }}><I.check size={16} stroke={2.6} /> Recebi</button>
                <button style={{
                  background: 'var(--paper-2)', color: 'var(--ink)',
                  border: '1px solid var(--rule)', borderRadius: 10, padding: '10px 8px',
                  fontSize: 13.5, fontWeight: 700, fontFamily: 'inherit',
                  display: 'flex', alignItems: 'center', justifyContent: 'center', gap: 5,
                }}><I.whatsapp size={15} /> Cobrar</button>
                <button style={{
                  background: 'var(--paper-2)', color: 'var(--ink-2)',
                  border: '1px solid var(--rule)', borderRadius: 10, padding: '10px 8px',
                  fontSize: 13.5, fontWeight: 700, fontFamily: 'inherit',
                  display: 'flex', alignItems: 'center', justifyContent: 'center', gap: 5,
                }}><I.cal size={15} /> Adiar</button>
              </div>
            </div>
          );
        })}
      </div>
    </PhoneShell>
  );
}

// (Removed) Legacy B4 superseded by date-picker variant above.
function _ScreenB4LegacyUNUSED() { return null; /*
  const items = [
    { name: 'Maria Souza',    what: 'Batom Avon vermelho', value: 45, tone: 'amber' },
    { name: 'Joana Pereira',  what: 'Panela Tupperware',   value: 30, tone: 'amber' },
    { name: 'Carla Lima',     what: 'Blusa estampada',     value: 22, tone: 'amber' },
  ];
  const total = items.reduce((s, i) => s + i.value, 0);
  return (
    <PhoneShell>
      <ScreenTopBar title="Pagam hoje" leading="back" />
      <div style={{ padding: '0 22px 8px' }}>
        <div style={{
          display: 'flex', justifyContent: 'space-between', alignItems: 'baseline',
          paddingBottom: 14, borderBottom: '1px solid var(--rule)',
        }}>
          <div>
            <div style={{ fontFamily: 'Caveat, cursive', fontSize: 30, color: 'var(--ink)', lineHeight: 1 }}>terça, 7 de maio</div>
            <div style={{ fontSize: 13, color: 'var(--ink-2)', fontWeight: 600, marginTop: 4 }}>{items.length} pessoas</div>
          </div>
          <div style={{ textAlign: 'right' }}>
            <div style={{ fontSize: 11, fontWeight: 700, color: 'var(--ink-2)', textTransform: 'uppercase', letterSpacing: 0.6 }}>total</div>
            <Money value={total} size={22} weight={800} color="var(--green)" />
          </div>
        </div>
      </div>

      <div style={{ flex: 1, overflow: 'auto', padding: '12px 16px 16px', display: 'flex', flexDirection: 'column', gap: 10 }}>
        {items.map((it) => (
          <div key={it.name} style={{
            background: 'var(--paper)', border: '1px solid var(--rule)',
            borderRadius: 14, padding: 14,
            display: 'flex', flexDirection: 'column', gap: 12,
            boxShadow: 'var(--shadow-paper)',
          }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: 12 }}>
              <Avatar name={it.name} size={40} tone="amber" />
              <div style={{ flex: 1, minWidth: 0 }}>
                <div style={{ fontSize: 15.5, fontWeight: 800, lineHeight: 1.1 }}>{it.name}</div>
                <div style={{ fontSize: 12.5, color: 'var(--ink-2)', fontWeight: 600, marginTop: 2,
                  whiteSpace: 'nowrap', overflow: 'hidden', textOverflow: 'ellipsis' }}>{it.what}</div>
              </div>
              <Money value={it.value} size={17} weight={800} />
            </div>
            <div style={{ display: 'grid', gridTemplateColumns: '1.4fr 1fr 1fr', gap: 6 }}>
              <button style={{
                background: 'var(--green)', color: '#fff',
                border: 'none', borderRadius: 10, padding: '10px 8px',
                fontSize: 14, fontWeight: 800, fontFamily: 'inherit',
                display: 'flex', alignItems: 'center', justifyContent: 'center', gap: 5,
              }}><I.check size={16} stroke={2.6} /> Recebi</button>
              <button style={{
                background: 'var(--paper-2)', color: 'var(--ink)',
                border: '1px solid var(--rule)', borderRadius: 10, padding: '10px 8px',
                fontSize: 13.5, fontWeight: 700, fontFamily: 'inherit',
                display: 'flex', alignItems: 'center', justifyContent: 'center', gap: 5,
              }}><I.whatsapp size={15} /> Cobrar</button>
              <button style={{
                background: 'var(--paper-2)', color: 'var(--ink-2)',
                border: '1px solid var(--rule)', borderRadius: 10, padding: '10px 8px',
                fontSize: 13.5, fontWeight: 700, fontFamily: 'inherit',
                display: 'flex', alignItems: 'center', justifyContent: 'center', gap: 5,
              }}><I.cal size={15} /> Adiar</button>
            </div>
          </div>
        ))}
      </div>
    </PhoneShell>
  );
} */ }

// ─────────────────────────────────────────────────────────────
// B5 — Configurações (mínima, sem abas)
// ─────────────────────────────────────────────────────────────
function ScreenB5() {
  return (
    <PhoneShell>
      <ScreenTopBar title="Ajustes" leading="back" />
      <div style={{ flex: 1, overflow: 'auto', padding: '0 22px 16px', display: 'flex', flexDirection: 'column', gap: 26 }}>

        {/* Mensagem de cobrança */}
        <section>
          <FieldLabel>Mensagem de cobrança</FieldLabel>
          <div style={{
            background: 'var(--paper-2)', border: '1px solid var(--rule)',
            borderRadius: 12, padding: '14px 16px',
            fontSize: 15, lineHeight: 1.55, color: 'var(--ink)', fontWeight: 500,
            position: 'relative',
          }}>
            <span>Oi {'{nome}'}, tudo bem? 🙂 Passando aqui pra lembrar da sua compra ({'{produto}'}, {'{valor}'}) que vence em {'{data}'}. Qualquer coisa me avisa!</span>
            <button style={{
              position: 'absolute', top: 10, right: 10,
              background: 'var(--paper)', border: '1px solid var(--rule)', borderRadius: 8,
              width: 32, height: 32, display: 'flex', alignItems: 'center', justifyContent: 'center', color: 'var(--ink-2)',
            }}><I.pencil size={15} /></button>
          </div>
          <div style={{ fontSize: 12, color: 'var(--ink-2)', marginTop: 8, fontWeight: 600, lineHeight: 1.5 }}>
            usamos {'{nome}'}, {'{produto}'}, {'{valor}'} e {'{data}'} para preencher.
          </div>
        </section>

        {/* Backup */}
        <section>
          <FieldLabel>Backup</FieldLabel>
          <div style={{
            background: 'var(--paper-2)', border: '1px solid var(--rule)', borderRadius: 12,
            padding: '14px 16px', display: 'flex', alignItems: 'center', gap: 14,
          }}>
            <div style={{
              width: 36, height: 36, borderRadius: 10,
              background: 'var(--paper)', border: '1px solid var(--rule)',
              color: 'var(--ink-2)', display: 'flex', alignItems: 'center', justifyContent: 'center',
            }}><I.download size={18} /></div>
            <div style={{ flex: 1 }}>
              <div style={{ fontSize: 14.5, fontWeight: 700 }}>Exportar dados</div>
              <div style={{ fontSize: 12.5, color: 'var(--ink-2)', fontWeight: 600 }}>último backup há 3 dias</div>
            </div>
            <I.chevR size={18} stroke={2} style={{ color: 'var(--ink-2)' }} />
          </div>
        </section>

        {/* Sobre */}
        <section>
          <FieldLabel>Sobre o app</FieldLabel>
          <div style={{
            background: 'var(--paper-2)', border: '1px solid var(--rule)', borderRadius: 12,
            padding: '14px 16px', display: 'flex', alignItems: 'center', gap: 14,
          }}>
            <div style={{
              width: 36, height: 36, borderRadius: 10,
              background: 'var(--paper)', border: '1px solid var(--rule)',
              color: 'var(--ink-2)', display: 'flex', alignItems: 'center', justifyContent: 'center',
            }}><I.info size={18} /></div>
            <div style={{ flex: 1 }}>
              <div style={{ fontSize: 14.5, fontWeight: 700 }}>Caderninho de Vendas</div>
              <div style={{ fontSize: 12.5, color: 'var(--ink-2)', fontWeight: 600 }}>versão 1.0.0</div>
            </div>
            <I.chevR size={18} stroke={2} style={{ color: 'var(--ink-2)' }} />
          </div>
        </section>

        <div style={{ flex: 1 }} />

        <div style={{ textAlign: 'center', padding: '8px 0 0' }}>
          <BrandMark size={26} color="var(--ink-2)" />
          <div style={{ fontSize: 11, color: 'var(--ink-2)', marginTop: 2, fontWeight: 600 }}>
            feito com carinho · 2026
          </div>
        </div>
      </div>
    </PhoneShell>
  );
}

Object.assign(window, {
  ScreenB1, ScreenB2, ScreenB3, ScreenB4, ScreenB5,
  ScreenTopBar, FieldLabel, PaperInput, ChipRow, BigToggle, Stepper,
});
