import { cn } from "@/lib/utils"

function Skeleton({ className, bold, ...props }: React.ComponentProps<"div"> & { bold?: boolean }) {
  return (
    <div
      data-slot="skeleton"
      className={cn("bg-accent animate-pulse rounded-lg", bold ? "h-8" : "h-5", className)}
      {...props}
    />
  )
}

export { Skeleton }
