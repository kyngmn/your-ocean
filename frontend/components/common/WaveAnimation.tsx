
interface WaveProps {
  /** 높이(px) */
  height?: number;
  /** 메인 색상 (CSS color) */
  color?: string;
  /** 애니메이션 속도(초) — 작을수록 빠름 */
  speed?: number;
  className?: string;
};

export default function WaveAnimation({
  height = 160,
  color = "#60A5FA",
  speed = 8,
  className = "",
}: WaveProps ) {
  const waveStyle = {
    height: `${height}px`,
    minHeight: `${height}px`,
    maxHeight: `${height}px`,
  } as React.CSSProperties;

  // 각 레이어에 다른 지속시간/불투명도/높이로 parallax 효과
  const layerProps = [
    { opacity: 0.65, offset: 0, duration: speed },
    { opacity: 0.45, offset: 10, duration: speed * 1.3 },
    { opacity: 0.25, offset: 20, duration: speed * 0.8 },
  ];

  return (
    <div
      className={`w-full overflow-hidden select-none ${className}`}
      style={waveStyle}
      aria-hidden
    >
      <svg
        className="block w-[200%] -translate-x-0"
        viewBox="0 0 1440 320"
        preserveAspectRatio="xMidYMid slice"
        xmlns="http://www.w3.org/2000/svg"
        style={{ 
          display: "block",
          height: `${height}px`,
          minHeight: `${height}px`,
          maxHeight: `${height}px`
        }}
      >
        <defs>
          <linearGradient id="waveGrad" x1="0%" x2="100%" y1="0%" y2="0%">
            <stop offset="0%" stopColor={color} stopOpacity={0.9} />
            <stop offset="100%" stopColor={color} stopOpacity={0.5} />
          </linearGradient>
        </defs>

        {layerProps.map((p, i) => (
          <g
            key={i}
            style={{
              transformOrigin: "50% 50%",
              opacity: p.opacity,
              animation: `waveMove ${p.duration}s linear infinite`,
              // translateY로 수직 흔들림을 추가
              willChange: "transform",
            }}
          >
            {/* 두 번 이어붙여 무한 루프 느낌 */}
            <path
              d={`M0,160 C240,${120 - p.offset} 480,${200 + p.offset} 720,160 C960,120 ${1200},200 1440,160 L1440,320 L0,320 Z`}
              fill="url(#waveGrad)"
              transform={`translate(0, ${i * 6})`}
            />
            <path
              d={`M0,160 C240,${120 - p.offset} 480,${200 + p.offset} 720,160 C960,120 ${1200},200 1440,160 L1440,320 L0,320 Z`}
              fill="url(#waveGrad)"
              transform={`translate(1440, ${i * 6})`}
            />
          </g>
        ))}

        <style>{`
          @keyframes waveMove {
            0% { transform: translateX(0) translateY(0); }
            50% { transform: translateX(-25%) translateY(3px); }
            100% { transform: translateX(-50%) translateY(0); }
          }

          /* 작은 화면에서 높이 조절 */
          @media (max-width: 640px) {
            svg { height: ${Math.max(80, Math.round(height * 0.6))}px; }
          }
        `}</style>
      </svg>
    </div>
  );
}
