// homecontext.jsx — Android home-screen scaffolding
// A wallpaper backdrop + small status bar so widgets are presented in context.
// Subtle warm gradient wallpaper (no photo placeholder), iconographic dock,
// and a generic OS clock+date pill.

// Wallpaper — warm dusk gradient, intentionally low-contrast so the widget pops.
function HomeWallpaper({ children, dark = false }) {
  // A muted warm gradient — soft enough to feel like a real wallpaper without
  // competing with the widget content. Two stops only, no rainbow.
  const bg = dark
    ? 'radial-gradient(ellipse at 30% 20%, #4a3a2e, #251c16 70%)'
    : 'radial-gradient(ellipse at 30% 20%, #d8c5a3, #9c7e5a 90%)';
  return (
    <div className="screen" style={{ background: bg, color: dark ? '#fff' : '#fff', overflow: 'hidden' }}>
      <PhoneStatus dark={true} />
      <div style={{ flex: 1, position: 'relative', display: 'flex', flexDirection: 'column' }}>
        {children}
      </div>
      <HomeIndicator dark={true} />
    </div>
  );
}

// Clock pill — generic OS clock + date, top of the home screen.
function HomeClock({ time = '9:30', date = 'ter, 7 de mai' }) {
  return (
    <div style={{
      textAlign: 'center', color: '#fff',
      textShadow: '0 1px 8px rgba(0,0,0,0.25)',
      fontFamily: 'Roboto, system-ui',
    }}>
      <div style={{ fontSize: 56, fontWeight: 300, letterSpacing: -2, lineHeight: 1 }}>{time}</div>
      <div style={{ fontSize: 13, fontWeight: 500, opacity: 0.95, marginTop: 4, textTransform: 'capitalize' }}>{date}</div>
    </div>
  );
}

// Tiny app icon — generic rounded square with a glyph inside. Used for the dock
// and the home grid in A4. Never a real brand logo.
function HomeAppIcon({ bg = '#3F7D55', glyph, label, size = 48 }) {
  return (
    <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', gap: 5 }}>
      <div style={{
        width: size, height: size, borderRadius: size * 0.27,
        background: bg, color: '#fff',
        display: 'flex', alignItems: 'center', justifyContent: 'center',
        boxShadow: '0 1px 0 rgba(255,255,255,0.15) inset, 0 4px 10px rgba(0,0,0,0.18)',
      }}>{glyph}</div>
      {label && <span style={{ fontSize: 11, color: '#fff', textShadow: '0 1px 4px rgba(0,0,0,0.5)', fontWeight: 500 }}>{label}</span>}
    </div>
  );
}

// Dock — bottom row of generic icons (clock, camera, chrome-ish, gallery, phone).
// Pure glyphs, no brand marks.
function HomeDock() {
  const icon = (children, bg) => <HomeAppIcon bg={bg} glyph={children} size={50} />;
  return (
    <div style={{
      display: 'flex', justifyContent: 'space-around',
      padding: '12px 18px 10px',
      margin: '0 12px',
      background: 'rgba(255,255,255,0.12)', borderRadius: 22,
      backdropFilter: 'blur(20px)',
    }}>
      {icon(
        <svg width="26" height="26" viewBox="0 0 24 24" fill="none" stroke="#fff" strokeWidth="1.8" strokeLinecap="round">
          <circle cx="12" cy="12" r="9" />
          <path d="M12 7v5l3 2" />
        </svg>, '#222'
      )}
      {icon(
        <svg width="26" height="26" viewBox="0 0 24 24" fill="none" stroke="#fff" strokeWidth="1.8">
          <rect x="3" y="6" width="18" height="13" rx="2"/><circle cx="12" cy="12.5" r="3.5"/>
          <path d="M9 6l1.5-2h3L15 6" strokeLinejoin="round"/>
        </svg>, '#5a5a5a'
      )}
      {icon(
        <svg width="26" height="26" viewBox="0 0 24 24" fill="none" stroke="#fff" strokeWidth="1.8">
          <circle cx="12" cy="12" r="9"/><path d="M3 12h18M12 3a14 14 0 010 18M12 3a14 14 0 000 18"/>
        </svg>, '#3a6fb5'
      )}
      {icon(
        <svg width="26" height="26" viewBox="0 0 24 24" fill="none" stroke="#fff" strokeWidth="1.8">
          <rect x="3" y="5" width="18" height="14" rx="2"/>
          <path d="M3 16l5-5 4 4 3-3 6 6"/>
          <circle cx="9" cy="10" r="1.5" fill="#fff" stroke="none"/>
        </svg>, '#7a4ba5'
      )}
      {icon(
        <svg width="26" height="26" viewBox="0 0 24 24" fill="none" stroke="#fff" strokeWidth="1.8" strokeLinecap="round" strokeLinejoin="round">
          <path d="M5 4.5c0-.6.4-1 1-1h2.6c.4 0 .8.3.9.7l1 3c.1.4 0 .8-.3 1l-1.4 1.2c1 2 2.6 3.6 4.6 4.6l1.2-1.4c.3-.3.7-.4 1-.3l3 1c.4.1.7.5.7 1V17c0 .6-.4 1-1 1-7 0-13-6-13-13z"/>
        </svg>, '#3F7D55'
      )}
    </div>
  );
}

// Backdrop just for showing a single widget centered on a wallpaper.
// Adds a soft glow under the widget so it reads as floating on the home.
function WidgetStage({ children }) {
  return (
    <HomeWallpaper>
      <div style={{ flex: 1, display: 'flex', flexDirection: 'column', justifyContent: 'space-between', padding: '24px 0 8px' }}>
        <HomeClock />
        <div style={{ display: 'flex', justifyContent: 'center', padding: '16px 16px' }}>
          {children}
        </div>
        <HomeDock />
      </div>
    </HomeWallpaper>
  );
}

Object.assign(window, { HomeWallpaper, HomeClock, HomeAppIcon, HomeDock, WidgetStage });
