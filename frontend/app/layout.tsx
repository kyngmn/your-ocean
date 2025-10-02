import "./globals.css"

import type { Metadata } from "next"
import Providers from "./providers"
import { Toaster } from "@/components/ui/sonner"
import localFont from "next/font/local"

const PyeojinGothic = localFont({
  src: [
    { path: "../public/fonts/PyeojinGothic-Light.woff2", weight: "300", style: "normal" },
    { path: "../public/fonts/PyeojinGothic-Regular.woff2", weight: "400", style: "normal" },
    { path: "../public/fonts/PyeojinGothic-Medium.woff2", weight: "500", style: "normal" },
    { path: "../public/fonts/PyeojinGothic-SemiBold.woff2", weight: "600", style: "normal" },
    { path: "../public/fonts/PyeojinGothic-Bold.woff2", weight: "700", style: "normal" }
  ]
})

export const metadata: Metadata = {
  title: "너의 OCEAN은",
  description: "당신의 AI 페르소나와 함께 성격의 바다를 항해하며 진정한 나를 발견해보세요."
}

export default function RootLayout({
  children
}: Readonly<{
  children: React.ReactNode
}>) {
  return (
    <html lang="ko" className={PyeojinGothic.className}>
      <body>
        <Providers>{children}</Providers>
        <Toaster position="bottom-center" richColors />
      </body>
    </html>
  )
}
