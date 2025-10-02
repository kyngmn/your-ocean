"use client"

import { QueryClient, QueryClientProvider } from "@tanstack/react-query"
import { useState } from "react"
import dynamic from "next/dynamic"

const ReactQueryDevtools = dynamic(
  () => {
    if (process.env.NODE_ENV === "development") {
      return import("@tanstack/react-query-devtools").then((d) => ({
        default: d.ReactQueryDevtools,
      }))
    }
    return Promise.resolve({ default: () => null })
  },
  { ssr: false }
)

export default function Providers({ children }: { children: React.ReactNode }) {
  const [queryClient] = useState(
    () =>
      new QueryClient({
        defaultOptions: {
          queries: {
            staleTime: 60 * 1000, // 1 minute
            retry: false
          }
        }
      })
  )

  return (
    <QueryClientProvider client={queryClient}>
      {children}
      {process.env.NODE_ENV === "development" && (
        <ReactQueryDevtools initialIsOpen={false} />
      )}
    </QueryClientProvider>
  )
}
