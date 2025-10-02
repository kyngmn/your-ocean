import type { NextConfig } from "next";

const nextConfig: NextConfig = {
  experimental: {
    serverActions: {
      bodySizeLimit: '10mb', // 10MB로 제한 증가
    },
  },
  images: {
    remotePatterns: [
      {
        protocol: 'https',
        hostname: 'be.myocean.cloud',
        port: '',
        pathname: '/files/**',
      },
    ],
  },
};

export default nextConfig;
