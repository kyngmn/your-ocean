import React from 'react';
import { DotLottieReact } from '@lottiefiles/dotlottie-react';
import type { Lottie } from '@/types/lottie';

export default function LottiePlayer({ src, loop, autoplay, className }: Lottie) {
    
  return (
    <DotLottieReact
      src={src}
      loop={loop}
      autoplay={autoplay}
      className={className}
    />
  );
}