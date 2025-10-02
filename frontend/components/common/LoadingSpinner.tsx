"use client"

import { LoaderCircle } from "lucide-react"
import React from "react"

interface LoadingProps {
  label?: string
  size?: "sm" | "md" | "lg"
  variant?: "spinner" | "dots" | "icon"
  className?: string
  fullScreen?: boolean
}

export function LoadingSpinner({
  label = "로딩 중...",
  size = "md",
  variant = "spinner",
  className = "",
  fullScreen = false
}: LoadingProps) {
  const sizeClasses = {
    sm: "h-4 w-4",
    md: "h-5 w-5",
    lg: "h-6 w-6"
  }

  const textSizeClasses = {
    sm: "text-xs",
    md: "text-sm",
    lg: "text-md"
  }

  const containerClasses = fullScreen
    ? "fixed inset-0 flex items-center justify-center bg-background/80 backdrop-blur-sm z-50"
    : "flex items-center justify-center"

  const renderSpinner = () => (
    <span
      className={`inline-block ${sizeClasses[size]} animate-spin rounded-full border-2 border-primary/10 border-t-primary`}
      aria-hidden
    />
  )

  const renderDots = () => (
    <div className="flex gap-1">
      {[0, 1, 2].map((i) => (
        <div
          key={i}
          className={`${sizeClasses[size]} rounded-full bg-primary animate-pulse`}
          style={{ animationDelay: `${i * 0.15}s` }}
        />
      ))}
    </div>
  )

  const renderIcon = () => <LoaderCircle className={`${sizeClasses[size]} text-primary animate-spin`} />

  const renderLoader = () => {
    switch (variant) {
      case "dots":
        return renderDots()
      case "icon":
        return renderIcon()
      case "spinner":
      default:
        return renderSpinner()
    }
  }

  return (
    <div className={`${containerClasses} ${className}`} role="status" aria-live="polite">
      <div className="inline-flex items-center gap-3">
        {renderLoader()}
        {label && <span className={`${textSizeClasses[size]} text-muted-foreground`}>{label}</span>}
      </div>
    </div>
  )
}

// 간단한 스피너 컴포넌트 (인라인 용도)
export function Spinner({ size = "md", className = "" }: { size?: "sm" | "md" | "lg"; className?: string }) {
  const sizeClasses = {
    sm: "h-3 w-3",
    md: "h-4 w-4",
    lg: "h-5 w-5"
  }

  return (
    <span
      className={`inline-block ${sizeClasses[size]} animate-spin rounded-full border-2 border-current border-t-transparent ${className}`}
      aria-hidden
    />
  )
}

export default LoadingSpinner
