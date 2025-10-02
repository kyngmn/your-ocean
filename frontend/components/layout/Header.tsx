"use client"

import { ArrowLeftIcon } from "lucide-react"
import Typography from "../ui/Typography"
import { useRouter } from "next/navigation"

interface HeaderProps {
  title?: string
  type?: "default" | "back"
  rightSlot?: React.ReactNode
}

export default function Header({ title, type, rightSlot }: HeaderProps) {
  const router = useRouter()

  const goBack = () => {
    router.back()
  }

  return (
    <header>
      <div className="header">
        {type === "back" ? (
          <div className="flex gap-4">
            <div className="flex items-center">
              <ArrowLeftIcon className="w-6 h-6" onClick={goBack} />
            </div>
            {title && <Typography type="h3">{title}</Typography>}
            {rightSlot && <div className="ml-auto">{rightSlot}</div>}
          </div>
        ) : (
          <div className="flex items-center">
            {title && <Typography type="h3">{title}</Typography>}
            {rightSlot && <div className="ml-auto">{rightSlot}</div>}
          </div>
        )}
      </div>
    </header>
  )
}
