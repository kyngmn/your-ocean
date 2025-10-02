
type SpinnerProps = {
  size?: number; // base size in px
  color?: string;
  className?: string;
  ariaLabel?: string;
};

// Ellipsis spinner: three (or four) dots that animate in sequence
export default function SpinnerEllipsis({
  size = 16,
  color = "currentColor",
  className = "",
  ariaLabel = "로딩 중",
}: SpinnerProps) {
  const dotSize = Math.max(4, Math.round(size / 3));
  const gap = Math.round(dotSize / 1.5);

  return (
    <div
      role="status"
      aria-live="polite"
      aria-label={ariaLabel}
      className={`inline-flex items-center justify-center ${className}`}
      style={{ lineHeight: 0 }}
    >
      <span
        className="ellipsis-dot"
        style={{
          width: dotSize,
          height: dotSize,
          marginRight: gap,
          borderRadius: "50%",
          display: "inline-block",
          background: color,
        }}
      />
      <span
        className="ellipsis-dot"
        style={{
          width: dotSize,
          height: dotSize,
          marginRight: gap,
          borderRadius: "50%",
          display: "inline-block",
          background: color,
        }}
      />
      <span
        className="ellipsis-dot"
        style={{
          width: dotSize,
          height: dotSize,
          borderRadius: "50%",
          display: "inline-block",
          background: color,
        }}
      />

      {/* CSS for animation (global inlined) */}
      <style>{`
        @keyframes ellipsis-fade {
          0% { opacity: 0.15; transform: translateY(0); }
          50% { opacity: 1; transform: translateY(-6px); }
          100% { opacity: 0.15; transform: translateY(0); }
        }

        .ellipsis-dot:nth-child(1) {
          animation: ellipsis-fade 1s ease-in-out infinite;
          animation-delay: 0s;
        }
        .ellipsis-dot:nth-child(2) {
          animation: ellipsis-fade 1s ease-in-out infinite;
          animation-delay: 0.15s;
        }
        .ellipsis-dot:nth-child(3) {
          animation: ellipsis-fade 1s ease-in-out infinite;
          animation-delay: 0.3s;
        }
      `}</style>
    </div>
  );
}
