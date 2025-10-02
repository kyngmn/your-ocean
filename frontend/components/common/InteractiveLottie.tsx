"use client"

import React, { useState, useEffect } from 'react';
import { DotLottieReact } from '@lottiefiles/dotlottie-react';
import type { Lottie } from '@/types/lottie';

type InteractiveLottieProps = Lottie & {
  onPop?: () => void;
  maxSize?: number;
  initialSize?: number;
  growthRate?: number;
  isInteractive?: boolean;
};

export default function InteractiveLottie({ 
  src, 
  loop, 
  autoplay, 
  className,
  onPop,
  maxSize = 200,
  initialSize = 100,
  growthRate = 20,
  isInteractive = true
}: InteractiveLottieProps) {
  const [currentSize, setCurrentSize] = useState(initialSize);
  const [isGrowing, setIsGrowing] = useState(false);
  const [isPopped, setIsPopped] = useState(false);
  const [isClient, setIsClient] = useState(false);

  useEffect(() => {
    setIsClient(true);
  }, []);

  const handleClick = () => {
    if (!isInteractive || isPopped) return;
    
    if (currentSize >= maxSize) {
      // 터지는 효과
      setIsPopped(true);
      onPop?.();
    } else {
      // 크기 증가
      setCurrentSize(prev => Math.min(prev + growthRate, maxSize));
    }
  };

  const resetSize = () => {
    setCurrentSize(initialSize);
    setIsPopped(false);
  };

  if (!isClient) {
    return <div className={`${className}`} style={{ width: initialSize, height: initialSize }} />;
  }

  if (isPopped) {
    return (
      <div className={`${className} flex flex-col items-center justify-center`}>
        <div className="text-center text-gray-500 text-sm mb-2">
          💥 터졌어요!
        </div>
        <button 
          onClick={resetSize}
          className="px-3 py-1 bg-blue-500 text-white text-xs rounded hover:bg-blue-600 transition-colors"
        >
          다시 시작
        </button>
      </div>
    );
  }

  return (
    <div 
      className={`${className} cursor-pointer transition-all duration-300 hover:scale-105 ${
        isGrowing ? 'animate-pulse' : ''
      }`}
      style={{ 
        width: currentSize, 
        height: currentSize,
        transform: isGrowing ? 'scale(1.05)' : 'scale(1)'
      }}
      onClick={handleClick}
      onMouseDown={() => setIsGrowing(true)}
      onMouseUp={() => setIsGrowing(false)}
      onMouseLeave={() => setIsGrowing(false)}
    >
      <DotLottieReact
        src={src}
        loop={loop}
        autoplay={autoplay}
        className="w-full h-full"
      />
      
      {/* 크기 표시 */}
      <div className="absolute -bottom-6 left-1/2 transform -translate-x-1/2 text-xs text-gray-600 whitespace-nowrap">
        크기: {Math.round((currentSize / maxSize) * 100)}%
      </div>
    </div>
  );
}
