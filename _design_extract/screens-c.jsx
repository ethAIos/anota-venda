// screens-c.jsx — Bloco C: special states (C1 onboarding, C2 empty, C3 sheet)
// Plus the A4 full home screen with all 3 widgets in context.

// ─────────────────────────────────────────────────────────────
// C1 — Onboarding (2-step variant shown side by side via prop)
// ─────────────────────────────────────────────────────────────
function ScreenC1({ step = 1 }) {
  return (
    <PhoneShell>
      <div style={{ flex: 1, display: 'flex', flexDirection: 'column', padding: '20px 22px 22px' }}>
        {/* Skip */}
        <div style={{ display: 'flex', justifyContent: 'flex-end' }}>
          <button style={{
            background: 'transparent', border: 'none', color: 'var(--ink-2)',
            fontSize: 14, fontWeight: 700, fontFamily: 'inherit', padding: '8px 4px',
          }}>pular</button>
        </div>

        {/* Illustration block — paper composition (lines + a check + a name) */}
        <div style={{ flex: 1, display: 'flex', alignItems: 'center', justifyContent: 'center', padding: '8px 0' }}>
          {step === 1 ? <OnboardingPaper /> : <OnboardingHome />}
        </div>

        {/* Copy */}
        <div style={{ textAlign: 'center', padding: '0 12px' }}>
          {step === 1 ? (
            <>
              <h1 style={{
                fontSize: 30, fontWeight: 800, letterSpacing: -0.6, margin: 0,
                lineHeight: 1.15, textWrap: 'pretty',
              }}>
                Vamos te lembrar de quem te deve.
              </h1>
              <p style={{
                fontSize: 16, color: 'var(--ink-2)', fontWeight: 600,
                margin: '14px 0 0', lineHeight: 1.5,
              }}>
                Sem planilha, sem complicação. Você anota, a gente avisa.
              </p>
            </>
          ) : (
            <>
              <h1 style={{ fontSize: 28, fontWeight: 800, letterSpacing: -0.5, margin: 0, lineHeight: 1.2 }}>
                Adiciona o widget na sua tela.
              </h1>
              <p style={{ fontSize: 15.5, color: 'var(--ink-2)', fontWeight: 600, margin: '14px 0 0', lineHeight: 1.5 }}>
                Segura num espaço vazio da home → "Widgets" → arrasta o caderninho.
              </p>
            </>
          )}
        </div>

        {/* Dots */}
        <div style={{ display: 'flex', justifyContent: 'center', gap: 6, padding: '24px 0 16px' }}>
          {[1, 2].map((i) => (
            <span key={i} style={{
              width: i === step ? 24 : 6, height: 6, borderRadius: 3,
              background: i === step ? 'var(--green)' : 'var(--rule-2)',
              transition: 'all 0.2s',
            }} />
          ))}
        </div>

        <PrimaryButton>{step === 1 ? 'Próximo' : 'Bora começar'}</PrimaryButton>
      </div>
    </PhoneShell>
  );
}

// Step 1 illustration: a small piece of notebook paper with handwritten names + a check.
function OnboardingPaper() {
  return (
    <div style={{
      width: 240, height: 240, position: 'relative',
      background: 'var(--paper-2)',
      border: '1px solid var(--rule)',
      borderRadius: 14,
      boxShadow: '0 4px 16px rgba(60,50,30,0.08)',
      transform: 'rotate(-3deg)',
      padding: '24px 22px 18px',
      backgroundImage: 'repeating-linear-gradient(to bottom, transparent 0 33px, var(--rule) 33px 34px)',
    }}>
      {/* Red margin line */}
      <div style={{
        position: 'absolute', left: 16, top: 12, bottom: 12,
        width: 1, background: 'rgba(181,70,46,0.4)',
      }} />
      <div style={{ fontFamily: 'Caveat, cursive', fontSize: 24, color: 'var(--ink)', lineHeight: '34px' }}>
        <div style={{ display: 'flex', justifyContent: 'space-between' }}>
          <span style={{ textDecoration: 'line-through', textDecorationColor: 'var(--green)', textDecorationThickness: 2 }}>Maria · 45</span>
        </div>
        <div style={{ display: 'flex', justifyContent: 'space-between' }}>
          <span>Joana · 30</span>
        </div>
        <div style={{ display: 'flex', justifyContent: 'space-between' }}>
          <span>Bia · 60</span>
        </div>
        <div style={{ display: 'flex', justifyContent: 'space-between' }}>
          <span>Carla · 22</span>
        </div>
      </div>
      {/* check sticker */}
      <div style={{
        position: 'absolute', top: -14, right: -10,
        width: 44, height: 44, borderRadius: '50%',
        background: 'var(--green)', color: '#fff',
        display: 'flex', alignItems: 'center', justifyContent: 'center',
        boxShadow: '0 6px 14px rgba(63,125,85,0.35)',
        transform: 'rotate(8deg)',
      }}>
        <I.check size={22} stroke={3} />
      </div>
    </div>
  );
}

// Step 2 illustration: tiny phone outline with widget pulsing in middle, hand-drawn arrow pointing to it.
function OnboardingHome() {
  return (
    <div style={{ position: 'relative', width: 260, height: 240 }}>
      {/* Tiny phone */}
      <div style={{
        position: 'absolute', left: 60, top: 0,
        width: 140, height: 220,
        background: 'linear-gradient(160deg, #d8c5a3, #9c7e5a)',
        borderRadius: 18,
        border: '6px solid #2A2724',
        boxShadow: '0 8px 24px rgba(60,50,30,0.18)',
        padding: 10,
      }}>
        {/* widget */}
        <div className="paper-bg" style={{
          background: 'var(--paper)',
          borderRadius: 10, height: 64, padding: 8,
          display: 'flex', flexDirection: 'column', justifyContent: 'space-between',
          boxShadow: '0 2px 6px rgba(0,0,0,0.18)',
        }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <span style={{ fontSize: 9, fontWeight: 800 }}>Pagam hoje</span>
            <span style={{ fontFamily: 'Caveat, cursive', fontSize: 10, color: 'var(--ink-2)' }}>caderninho</span>
          </div>
          <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: 8, fontWeight: 700 }}>
            <span>Maria</span>
            <span style={{ color: 'var(--green)' }}>R$ 45</span>
          </div>
          <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: 8, fontWeight: 700 }}>
            <span>Joana</span>
            <span style={{ color: 'var(--green)' }}>R$ 30</span>
          </div>
        </div>
        {/* Other empty widget slots */}
        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 6, marginTop: 8 }}>
          <div style={{ height: 28, borderRadius: 6, border: '1.5px dashed rgba(255,255,255,0.5)' }} />
          <div style={{ height: 28, borderRadius: 6, border: '1.5px dashed rgba(255,255,255,0.5)' }} />
        </div>
      </div>

      {/* Hand-drawn arrow pointing to widget */}
      <svg width="80" height="100" viewBox="0 0 80 100" style={{ position: 'absolute', left: -10, top: 16 }}>
        <path d="M5 80 Q 10 30, 60 28" fill="none" stroke="#3F7D55" strokeWidth="2.5" strokeLinecap="round" strokeDasharray="0" />
        <path d="M50 22 L62 28 L52 36" fill="none" stroke="#3F7D55" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round" />
      </svg>
      <span style={{
        position: 'absolute', left: -6, top: 86,
        fontFamily: 'Caveat, cursive', fontSize: 22, color: 'var(--green)',
        transform: 'rotate(-6deg)',
      }}>aqui!</span>
    </div>
  );
}

// ─────────────────────────────────────────────────────────────
// C2 — Estado vazio "Em aberto" (full screen variant; widget version is in WidgetA3 empty)
// ─────────────────────────────────────────────────────────────
function ScreenC2() {
  // Show as a phone with the empty widget placed in context — represents
  // the empty state living on the home screen.
  return (
    <HomeWallpaper>
      <div style={{ flex: 1, display: 'flex', flexDirection: 'column', justifyContent: 'space-between', padding: '24px 0 8px' }}>
        <HomeClock />
        <div style={{ display: 'flex', justifyContent: 'center', padding: '16px' }}>
          <WidgetA3 empty w={350} h={170} />
        </div>
        <HomeDock />
      </div>
    </HomeWallpaper>
  );
}

// ─────────────────────────────────────────────────────────────
// C3 — Bottom sheet "Recebi"
// Showed as a phone with the underlying B2 dimmed and the sheet pulled up.
// ─────────────────────────────────────────────────────────────
function ScreenC3() {
  return (
    <div className="screen" style={{ position: 'relative', overflow: 'hidden' }}>
      {/* Background — dimmed B2 */}
      <div style={{ position: 'absolute', inset: 0, filter: 'brightness(0.55) saturate(0.85)', pointerEvents: 'none' }}>
        <ScreenB2 />
      </div>
      {/* Scrim */}
      <div style={{ position: 'absolute', inset: 0, background: 'rgba(20,15,10,0.35)' }} />

      {/* Bottom sheet */}
      <div style={{
        position: 'absolute', left: 0, right: 0, bottom: 0,
        background: 'var(--paper)',
        borderTopLeftRadius: 22, borderTopRightRadius: 22,
        padding: '12px 22px 28px',
        boxShadow: '0 -8px 28px rgba(0,0,0,0.16)',
        display: 'flex', flexDirection: 'column', gap: 18,
      }} className="paper-bg">
        {/* Grab handle */}
        <div style={{ display: 'flex', justifyContent: 'center', paddingTop: 6 }}>
          <div style={{ width: 40, height: 4, borderRadius: 2, background: 'var(--rule-2)' }} />
        </div>
        <div>
          <div style={{ fontSize: 13, fontWeight: 700, color: 'var(--ink-2)', textTransform: 'uppercase', letterSpacing: 0.6 }}>
            Recebi de
          </div>
          <div style={{ fontSize: 22, fontWeight: 800, marginTop: 4, letterSpacing: -0.3 }}>Maria Souza</div>
          <div style={{ fontSize: 13, fontWeight: 600, color: 'var(--ink-2)', marginTop: 2 }}>
            Batom Avon vermelho · vence hoje
          </div>
        </div>

        <div>
          <FieldLabel>Quanto?</FieldLabel>
          <div style={{
            display: 'flex', alignItems: 'baseline', gap: 8,
            borderBottom: '2px solid var(--green)', paddingBottom: 8,
          }}>
            <span style={{ fontSize: 24, color: 'var(--ink-2)', fontWeight: 700 }}>R$</span>
            <span className="tnum" style={{ fontSize: 44, fontWeight: 800, letterSpacing: -1.5, color: 'var(--green)' }}>
              45,00
            </span>
            <button style={{
              marginLeft: 'auto', background: 'transparent', border: 'none',
              color: 'var(--ink-2)', padding: '4px 8px',
              display: 'flex', alignItems: 'center', gap: 4,
              fontSize: 13, fontWeight: 700, fontFamily: 'inherit',
            }}><I.pencil size={14} /> editar</button>
          </div>
          <div style={{ fontSize: 12, color: 'var(--ink-2)', marginTop: 6, fontWeight: 600 }}>
            valor cheio do pedido
          </div>
        </div>

        <div style={{ display: 'flex', gap: 10 }}>
          <GhostButton color="paper">Cancelar</GhostButton>
          <div style={{ flex: 1 }}>
            <PrimaryButton icon={<I.check size={18} stroke={2.6} />}>Confirmar</PrimaryButton>
          </div>
        </div>
      </div>
    </div>
  );
}

// ─────────────────────────────────────────────────────────────
// A4 — Tela inicial Android com os 3 widgets convivendo
// ─────────────────────────────────────────────────────────────
function ScreenA4() {
  return (
    <HomeWallpaper>
      <div style={{ flex: 1, display: 'flex', flexDirection: 'column', padding: '14px 0 8px' }}>
        {/* Mini clock (smaller than single-widget stage) */}
        <div style={{ textAlign: 'center', marginBottom: 14 }}>
          <div style={{ fontSize: 38, fontWeight: 300, letterSpacing: -1.5, color: '#fff', textShadow: '0 1px 8px rgba(0,0,0,0.25)', fontFamily: 'Roboto, system-ui', lineHeight: 1 }}>9:30</div>
          <div style={{ fontSize: 12, fontWeight: 500, color: '#fff', textShadow: '0 1px 4px rgba(0,0,0,0.4)', marginTop: 4, opacity: 0.95 }}>terça, 7 de maio</div>
        </div>

        {/* Widgets stack */}
        <div style={{ display: 'flex', flexDirection: 'column', gap: 12, padding: '0 16px' }}>
          <WidgetA2 w={358} h={184} />
          <div style={{ display: 'flex', gap: 12 }}>
            <WidgetA1 w={172} h={92} />
            {/* A small generic clock-app widget for realism */}
            <div style={{
              flex: 1, height: 92, borderRadius: 22,
              background: 'rgba(255,255,255,0.14)',
              backdropFilter: 'blur(20px)',
              padding: 12,
              display: 'flex', flexDirection: 'column', justifyContent: 'space-between',
              boxShadow: '0 2px 4px rgba(0,0,0,0.18), 0 10px 24px rgba(0,0,0,0.18)',
              color: '#fff',
            }}>
              <div style={{ fontSize: 11, fontWeight: 600, opacity: 0.85 }}>Tempo · São Paulo</div>
              <div style={{ display: 'flex', alignItems: 'baseline', gap: 6 }}>
                <span style={{ fontSize: 28, fontWeight: 300, letterSpacing: -1 }}>23°</span>
                <span style={{ fontSize: 11, opacity: 0.85 }}>parcialmente nublado</span>
              </div>
            </div>
          </div>
        </div>

        {/* App icon row */}
        <div style={{ flex: 1 }} />
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(4, 1fr)', gap: 12, padding: '0 22px 18px', justifyItems: 'center' }}>
          <HomeAppIcon bg="#3F7D55" size={50} label="Caderninho" glyph={
            <span style={{ fontFamily: 'Caveat, cursive', fontSize: 24, fontWeight: 700, lineHeight: 1 }}>c</span>
          } />
          <HomeAppIcon bg="#5a5a5a" size={50} label="Galeria" glyph={
            <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="#fff" strokeWidth="1.8">
              <rect x="3" y="5" width="18" height="14" rx="2"/><path d="M3 16l5-5 4 4 3-3 6 6"/>
            </svg>
          } />
          <HomeAppIcon bg="#3a6fb5" size={50} label="Mensagens" glyph={
            <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="#fff" strokeWidth="1.8" strokeLinejoin="round">
              <path d="M4 5h16v11H8.5L4 19.5V5z"/>
            </svg>
          } />
          <HomeAppIcon bg="#a8442e" size={50} label="Notas" glyph={
            <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="#fff" strokeWidth="1.8">
              <rect x="5" y="3" width="14" height="18" rx="2"/><path d="M9 8h6M9 12h6M9 16h4"/>
            </svg>
          } />
        </div>

        <HomeDock />
      </div>
    </HomeWallpaper>
  );
}

Object.assign(window, { ScreenC1, ScreenC2, ScreenC3, ScreenA4 });
