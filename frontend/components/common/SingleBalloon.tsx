"use client"

import { useState, useEffect } from "react";

type SingleBalloonProps = {
  size?: number; // diameter in pixels
  color?: string;
  onPop?: () => void;
  className?: string;
  isInteractive?: boolean;
  initialSize?: number;
  maxSize?: number;
  growthRate?: number;
};

export default function SingleBalloon({
  size = 100,
  color = "var(--background-blue)",
  onPop,
  className = "",
  isInteractive = true,
  initialSize = 50,
  maxSize = 200,
  growthRate = 1
}: SingleBalloonProps) {
  const [currentSize, setCurrentSize] = useState(initialSize);
  const [isGrowing, setIsGrowing] = useState(false);
  const [isPopped, setIsPopped] = useState(false);
  const [isPopping, setIsPopping] = useState(false);
  const [isClient, setIsClient] = useState(false);
  const [fragments, setFragments] = useState<Array<{
    id: number;
    x: number;
    y: number;
    rotation: number;
    scale: number;
  }>>([]);

  useEffect(() => {
    setIsClient(true);
  }, []);

  const handleClick = () => {
    if (!isInteractive || isPopped || isPopping) return;
    
    if (currentSize >= maxSize) {
      // 풍선이 터지는 애니메이션 시작
      startPopAnimation();
    } else {
      // 풍선이 커짐
      setCurrentSize(prev => Math.min(prev + growthRate * 10, maxSize));
    }
  };

  const startPopAnimation = () => {
    setIsPopping(true);
    
    // 파편들 생성
    const newFragments = Array.from({ length: 8 }).map((_, i) => ({
      id: i,
      x: Math.random() * 200 - 100, // -100 to 100
      y: Math.random() * 200 - 100,
      rotation: Math.random() * 360,
      scale: 0.3 + Math.random() * 0.7
    }));
    setFragments(newFragments);
    
    // 애니메이션 완료 후 상태 변경
    setTimeout(() => {
      setIsPopped(true);
      setIsPopping(false);
      onPop?.();
    }, 600); // 0.6초 후 완전히 터진 상태로 변경
  };

  const resetBalloon = () => {
    setCurrentSize(initialSize);
    setIsPopped(false);
    setIsGrowing(false);
    setIsPopping(false);
    setFragments([]);
  };

  const startGrowing = () => {
    setIsGrowing(true);
  };

  const stopGrowing = () => {
    setIsGrowing(false);
  };

  if (!isClient) {
    return <div className={`relative ${className}`} style={{ width: size, height: size }} />;
  }

  if (isPopped) {
    return (
      <div className={`relative ${className}`} style={{ width: size, height: size }}>
        <div className="text-center text-gray-500 text-sm">
          💥 터졌어요!
        </div>
        <button 
          onClick={resetBalloon}
          className="mt-2 px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-600 transition-colors"
        >
          다시 시작
        </button>
      </div>
    );
  }

  if (isPopping) {
    return (
      <div className={`relative ${className}`} style={{ width: size, height: size }}>
        {/* 터지는 파편들 */}
        {fragments.map((fragment) => (
          <div
            key={fragment.id}
            className="absolute"
            style={{
              left: '50%',
              top: '50%',
              width: `${currentSize * 0.2}px`,
              height: `${currentSize * 0.2}px`,
              background: `radial-gradient(circle at 25% 25%, rgba(255,255,255,0.9), ${color} 60%, ${color} 100%)`,
              borderRadius: '50%',
              transform: `translate(-50%, -50%) translate(${fragment.x}px, ${fragment.y}px) rotate(${fragment.rotation}deg) scale(${fragment.scale})`,
              animation: `popFragment 0.6s ease-out forwards`,
              boxShadow: `0 0 ${currentSize/6}px ${color}50`,
            }}
          />
        ))}
        
        {/* 터지는 순간의 폭발 효과 */}
        <div
          className="absolute"
          style={{
            left: '50%',
            top: '50%',
            width: `${currentSize * 1.5}px`,
            height: `${currentSize * 1.5}px`,
            background: `radial-gradient(circle, ${color}40, transparent 70%)`,
            borderRadius: '50%',
            transform: 'translate(-50%, -50%)',
            animation: 'explosion 0.6s ease-out forwards',
          }}
        />
      </div>
    );
  }

  return (
    <div className={`relative ${className}`} style={{ width: size, height: size }}>
      <div
        className={`cursor-pointer transition-all duration-300 hover:scale-105 ${
          isInteractive ? 'hover:shadow-lg' : ''
        }`}
        style={{
          width: `${currentSize}px`,
          height: `${currentSize}px`,
          background: `radial-gradient(circle at 25% 25%, rgba(255,255,255,0.9), ${color} 60%, ${color} 100%)`,
          borderRadius: '50%',
          boxShadow: `
            0 0 ${currentSize/3}px ${color}50,
            inset 0 0 ${currentSize/6}px rgba(255,255,255,0.8),
            0 0 ${currentSize/2}px ${color}30
          `,
          border: `2px solid ${color}80`,
          animation: isGrowing ? 'pulse 0.5s ease-in-out infinite' : 'none',
          transform: 'translate(-50%, -50%)',
          position: 'absolute',
          left: '50%',
          top: '50%',
        }}
        onClick={handleClick}
        onMouseDown={startGrowing}
        onMouseUp={stopGrowing}
        onMouseLeave={stopGrowing}
      >
        {/* 풍선의 하이라이트 */}
        <div
          style={{
            position: 'absolute',
            top: '15%',
            left: '20%',
            width: '30%',
            height: '30%',
            background: 'rgba(255,255,255,0.8)',
            borderRadius: '50%',
            transform: 'translate(-50%, -50%)',
          }}
        />
        
        {/* 풍선의 실 */}
        <div
          style={{
            position: 'absolute',
            bottom: '-20px',
            left: '50%',
            width: '2px',
            height: '20px',
            background: '#8B4513',
            transform: 'translateX(-50%)',
          }}
        />
      </div>

      {/* 크기 표시 */}
      <div className="absolute -bottom-8 left-1/2 transform -translate-x-1/2 text-xs text-gray-600">
        크기: {Math.round((currentSize / maxSize) * 100)}%
      </div>

      <style jsx>{`
        @keyframes pulse {
          0% { transform: translate(-50%, -50%) scale(1); }
          50% { transform: translate(-50%, -50%) scale(1.05); }
          100% { transform: translate(-50%, -50%) scale(1); }
        }
        
        @keyframes popFragment {
          0% {
            transform: translate(-50%, -50%) translate(0px, 0px) rotate(0deg) scale(1);
            opacity: 1;
          }
          100% {
            transform: translate(-50%, -50%) translate(var(--fragment-x), var(--fragment-y)) rotate(var(--fragment-rotation)) scale(0);
            opacity: 0;
          }
        }
        
        @keyframes explosion {
          0% {
            transform: translate(-50%, -50%) scale(0);
            opacity: 1;
          }
          50% {
            transform: translate(-50%, -50%) scale(1.2);
            opacity: 0.8;
          }
          100% {
            transform: translate(-50%, -50%) scale(2);
            opacity: 0;
          }
        }
      `}</style>
    </div>
  );
}
