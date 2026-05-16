// shared.jsx — phone shell, status bar, icons, building blocks
// All components are designed to feel like a notebook: warm paper bg,
// thin bege rules, no heavy shadows. Lucide-style line icons drawn inline.

// ─────────────────────────────────────────────────────────────
// Icons — line style, 1.6 stroke, currentColor. Tiny set, no library.
// ─────────────────────────────────────────────────────────────
const Icon = ({ d, size = 22, stroke = 1.7, fill = 'none', children, style }) => (
  <svg width={size} height={size} viewBox="0 0 24 24"
    fill={fill} stroke="currentColor" strokeWidth={stroke}
    strokeLinecap="round" strokeLinejoin="round" style={style}>
    {d ? <path d={d} /> : children}
  </svg>
);

const I = {
  plus:    (p) => <Icon {...p} d="M12 5v14M5 12h14" />,
  check:   (p) => <Icon {...p} d="M5 12.5l4 4 10-10" />,
  x:       (p) => <Icon {...p} d="M6 6l12 12M18 6L6 18" />,
  close:   (p) => <Icon {...p} d="M6 6l12 12M18 6L6 18" />,
  chevR:   (p) => <Icon {...p} d="M9 6l6 6-6 6" />,
  chevL:   (p) => <Icon {...p} d="M15 6l-6 6 6 6" />,
  chevD:   (p) => <Icon {...p} d="M6 9l6 6 6-6" />,
  phone:   (p) => <Icon {...p} d="M5 4.5c0-.6.4-1 1-1h2.6c.4 0 .8.3.9.7l1 3c.1.4 0 .8-.3 1l-1.4 1.2c1 2 2.6 3.6 4.6 4.6l1.2-1.4c.3-.3.7-.4 1-.3l3 1c.4.1.7.5.7 1V17c0 .6-.4 1-1 1-7 0-13-6-13-13z" />,
  msg:     (p) => <Icon {...p} d="M4 5.5h16v10H8.5L4 19.5z" />,
  whatsapp:(p) => <Icon {...p} d="M4 20l1.5-4A8 8 0 1 1 9 19.5L4 20zM9 10c.5 1.5 1.5 2.5 3 3l1.2-.9c.3-.2.7-.2 1 0l2 .8c.3.1.5.4.5.7v1.2c0 .5-.4 1-.9 1A7 7 0 0 1 8.4 9c0-.5.5-.9 1-.9h1.2c.3 0 .6.2.7.5l.8 2c.1.3 0 .7-.2 1l-.9 1.2" />,
  user:    (p) => <Icon {...p} d="M12 12a4 4 0 100-8 4 4 0 000 8zM4 20a8 8 0 0116 0" />,
  pencil:  (p) => <Icon {...p} d="M4 20l4-1 11-11-3-3L5 16l-1 4zM14 5l3 3" />,
  cal:     (p) => <Icon {...p} d="M5 6h14v14H5zM3.5 6h17M9 3v4M15 3v4" />,
  clock:   (p) => <Icon {...p}>
    <circle cx="12" cy="12" r="8" />
    <path d="M12 8v4l3 2" />
  </Icon>,
  bell:    (p) => <Icon {...p} d="M6 16h12l-1.5-2V11a4.5 4.5 0 10-9 0v3L6 16zM10 19a2 2 0 004 0" />,
  search:  (p) => <Icon {...p}>
    <circle cx="11" cy="11" r="6" />
    <path d="M20 20l-4.5-4.5" />
  </Icon>,
  arrowL:  (p) => <Icon {...p} d="M19 12H5M11 6l-6 6 6 6" />,
  more:    (p) => <Icon {...p}>
    <circle cx="6" cy="12" r="1.2" fill="currentColor" stroke="none" />
    <circle cx="12" cy="12" r="1.2" fill="currentColor" stroke="none" />
    <circle cx="18" cy="12" r="1.2" fill="currentColor" stroke="none" />
  </Icon>,
  download:(p) => <Icon {...p} d="M12 4v12M7 11l5 5 5-5M5 20h14" />,
  info:    (p) => <Icon {...p}>
    <circle cx="12" cy="12" r="8" />
    <path d="M12 11v5M12 8.5v.1" />
  </Icon>,
  wallet:  (p) => <Icon {...p} d="M4 7h13a2 2 0 012 2v8a2 2 0 01-2 2H6a2 2 0 01-2-2V6a2 2 0 012-2h11M16 13h2" />,
  receipt: (p) => <Icon {...p} d="M6 3h12v18l-2-1.5L14 21l-2-1.5L10 21l-2-1.5L6 21V3zM9 8h6M9 12h6M9 16h4" />,
  mute:    (p) => <Icon {...p} d="M12 5l-4 4H4v6h4l4 4V5zM16 9l4 6M20 9l-4 6" />,
};

// ─────────────────────────────────────────────────────────────
// PhoneShell — frameless 390×844 with status bar + home indicator
// Full-bleed paper bg by default. No app bar — screens own their headers.
// ─────────────────────────────────────────────────────────────
function PhoneShell({ children, statusDark = false, bg = 'var(--paper)', noStatus = false, noHome = false }) {
  return (
    <div className="screen paper-bg" style={{ background: bg }}>
      {!noStatus && <PhoneStatus dark={statusDark} />}
      <div style={{ flex: 1, minHeight: 0, display: 'flex', flexDirection: 'column' }}>
        {children}
      </div>
      {!noHome && <HomeIndicator dark={statusDark} />}
    </div>
  );
}

function PhoneStatus({ dark = false }) {
  const c = dark ? '#fff' : 'var(--ink)';
  return (
    <div style={{
      height: 36, padding: '0 22px',
      display: 'flex', alignItems: 'center', justifyContent: 'space-between',
      color: c, fontFamily: 'Roboto, system-ui', fontSize: 14, fontWeight: 600,
      flexShrink: 0,
    }}>
      <span style={{ letterSpacing: 0.2 }}>9:30</span>
      <div style={{ display: 'flex', gap: 5, alignItems: 'center' }}>
        <svg width="15" height="11" viewBox="0 0 15 11"><path d="M1 8h2v2H1zM5 6h2v4H5zM9 4h2v6H9zM13 1h2v9h-2z" fill={c}/></svg>
        <svg width="14" height="10" viewBox="0 0 14 10" fill="none" stroke={c} strokeWidth="1.4">
          <path d="M1 4a9 9 0 0112 0M3 6a6 6 0 018 0M5.5 8a2.5 2.5 0 013 0" strokeLinecap="round"/>
        </svg>
        <svg width="22" height="11" viewBox="0 0 22 11">
          <rect x="1" y="1" width="18" height="9" rx="2" fill="none" stroke={c} strokeWidth="1.2"/>
          <rect x="3" y="3" width="13" height="5" rx="1" fill={c}/>
          <rect x="20" y="4" width="1.5" height="3" rx="0.5" fill={c}/>
        </svg>
      </div>
    </div>
  );
}

function HomeIndicator({ dark = false }) {
  return (
    <div style={{ height: 22, display: 'flex', alignItems: 'center', justifyContent: 'center', flexShrink: 0 }}>
      <div style={{ width: 124, height: 4, borderRadius: 2, background: dark ? 'rgba(255,255,255,0.7)' : 'rgba(42,39,36,0.6)' }} />
    </div>
  );
}

// ─────────────────────────────────────────────────────────────
// Building blocks
// ─────────────────────────────────────────────────────────────

// Avatar with initial — soft paper-tinted
function Avatar({ name, size = 36, tone = 'default' }) {
  const tones = {
    default: { bg: '#EDE3D2', fg: '#6B5C44' },
    green:   { bg: '#DCE8DA', fg: '#3F7D55' },
    red:     { bg: '#EFD7CE', fg: '#9C3E29' },
    amber:   { bg: '#F1E4C2', fg: '#7A6022' },
  };
  const t = tones[tone] || tones.default;
  return (
    <div style={{
      width: size, height: size, borderRadius: '50%',
      background: t.bg, color: t.fg,
      display: 'flex', alignItems: 'center', justifyContent: 'center',
      fontWeight: 700, fontSize: size * 0.4,
      flexShrink: 0,
    }}>{(name || '?').slice(0, 1).toUpperCase()}</div>
  );
}

// Money — formats R$ with comma, optional accent color
function Money({ value, color, size, weight = 700 }) {
  const v = Math.round(value * 100);
  const reais = Math.floor(Math.abs(v) / 100);
  const cents = String(Math.abs(v) % 100).padStart(2, '0');
  return (
    <span className="tnum" style={{ color, fontSize: size, fontWeight: weight, letterSpacing: -0.2 }}>
      {v < 0 ? '−' : ''}R$ {reais.toLocaleString('pt-BR')},{cents}
    </span>
  );
}

// PrimaryButton — green, generous
function PrimaryButton({ children, icon, onClick, full = true, color = 'green' }) {
  const colors = {
    green: { bg: 'var(--green)', fg: '#fff' },
    red:   { bg: 'var(--red)', fg: '#fff' },
  };
  const c = colors[color];
  return (
    <button onClick={onClick} style={{
      width: full ? '100%' : 'auto',
      background: c.bg, color: c.fg,
      border: 'none', borderRadius: 12,
      padding: '16px 20px',
      fontSize: 17, fontWeight: 700, fontFamily: 'inherit',
      display: 'flex', alignItems: 'center', justifyContent: 'center', gap: 8,
      boxShadow: '0 1px 0 rgba(0,0,0,0.04), 0 4px 12px rgba(63,125,85,0.2)',
      cursor: 'pointer',
    }}>
      {icon}{children}
    </button>
  );
}

// GhostButton — paper-toned outline
function GhostButton({ children, icon, onClick, color = 'ink', size = 'md' }) {
  const colors = {
    ink: { bg: 'transparent', fg: 'var(--ink)', border: 'var(--rule-2)' },
    green: { bg: 'var(--green-soft)', fg: 'var(--green)', border: 'transparent' },
    red:  { bg: 'var(--red-soft)', fg: 'var(--red)', border: 'transparent' },
    paper: { bg: 'var(--paper-2)', fg: 'var(--ink)', border: 'var(--rule)' },
  };
  const c = colors[color];
  const sizes = { sm: { p: '8px 12px', f: 14 }, md: { p: '12px 16px', f: 15 }, lg: { p: '14px 18px', f: 16 } };
  const s = sizes[size];
  return (
    <button onClick={onClick} style={{
      background: c.bg, color: c.fg,
      border: `1px solid ${c.border}`,
      borderRadius: 10, padding: s.p,
      fontSize: s.f, fontWeight: 700, fontFamily: 'inherit',
      display: 'inline-flex', alignItems: 'center', justifyContent: 'center', gap: 6,
      cursor: 'pointer',
    }}>{icon}{children}</button>
  );
}

// Pill — small status badge
function Pill({ children, tone = 'default' }) {
  const tones = {
    default: { bg: 'var(--paper-2)', fg: 'var(--ink-2)' },
    red: { bg: 'var(--red-soft)', fg: 'var(--red)' },
    green: { bg: 'var(--green-soft)', fg: 'var(--green)' },
    amber: { bg: 'var(--highlight)', fg: '#7A6022' },
  };
  const t = tones[tone] || tones.default;
  return (
    <span style={{
      display: 'inline-flex', alignItems: 'center', gap: 4,
      background: t.bg, color: t.fg,
      fontSize: 12, fontWeight: 700,
      padding: '3px 8px', borderRadius: 99,
      letterSpacing: 0.1,
    }}>{children}</span>
  );
}

// Caderninho brand mark — handwritten name. Used in the app header.
function BrandMark({ size = 22, color = 'var(--ink)' }) {
  return (
    <span style={{
      fontFamily: 'Caveat, cursive', fontSize: size, color,
      fontWeight: 700, letterSpacing: 0.3, lineHeight: 1,
    }}>caderninho</span>
  );
}

Object.assign(window, {
  I, Icon, PhoneShell, PhoneStatus, HomeIndicator,
  Avatar, Money, PrimaryButton, GhostButton, Pill, BrandMark,
});
