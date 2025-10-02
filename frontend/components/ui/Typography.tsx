import { cn } from "@/lib/utils"

interface TypographyProps {
  children: React.ReactNode
  type: "h1" | "h2" | "h3" | "h4" | "p" | "large" | "small" | "muted" | "pale"
  className?: string
}

export default function Typography({ children, type, className }: TypographyProps) {
  return (
    <div>
      {type === "h1" && (
        <h1
          className={cn("scroll-m-20 text-center text-[1.5rem] font-extrabold tracking-tight text-balance", className)}
        >
          {children}
        </h1>
      )}
      {type === "h2" && (
        <h2 className={cn("scroll-m-20 pb-2 text-[1.375rem] font-semibold tracking-tight first:mt-0", className)}>
          {children}
        </h2>
      )}
      {type === "h3" && (
        <h3 className={cn("scroll-m-20 text-[1.25rem] font-semibold tracking-tight", className)}>{children}</h3>
      )}
      {type === "h4" && (
        <h4 className={cn("scroll-m-20 text-[1.125rem] font-semibold tracking-tight", className)}>{children}</h4>
      )}
      {type === "p" && <p className={cn("leading-7 [&:not(:first-child)]:mt-6", className)}>{children}</p>}
      {type === "large" && <div className={cn("text-lg font-semibold", className)}>{children}</div>}
      {type === "small" && <small className={cn("text-sm leading-none font-medium", className)}>{children}</small>}
      {type === "muted" && <p className={cn("text-muted-foreground text-sm", className)}>{children}</p>}
      {type === "pale" && <p className={cn("text-muted-foreground text-xs", className)}>{children}</p>}
    </div>
  )
}
