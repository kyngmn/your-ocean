import { ChangeEventHandler, KeyboardEventHandler, MouseEventHandler } from "react"
import { ChevronRight, Loader2 } from "lucide-react"

import { Button } from "../ui/button"
import { Textarea } from "../ui/textarea"
import { cn } from "@/lib/utils"

interface CustomTextareaProps {
  placeholder?: string
  value?: string
  onChange?: ChangeEventHandler<HTMLTextAreaElement>
  onKeyDown?: KeyboardEventHandler<HTMLTextAreaElement>
  type?: "chat" | "diary"
  className?: string
  onClick?: MouseEventHandler<HTMLButtonElement>
  disabled?: boolean
  loading?: boolean
}

export default function CustomTextarea({
  placeholder,
  value,
  onChange,
  onKeyDown,
  type,
  className,
  onClick,
  disabled,
  loading
}: CustomTextareaProps) {
  return (
    <div className="relative bg-background rounded-md">
      <Textarea
        onChange={onChange}
        onKeyDown={onKeyDown}
        placeholder={placeholder}
        value={value}
        className={cn(className, "min-h-[120px] resize-none")}
        disabled={disabled}
      />
      {type === "chat" && (
        <Button
          onClick={onClick}
          disabled={disabled}
          size="icon"
          variant="ghost"
          className="absolute bottom-3 right-3 h-8 w-8 p-0 bg-primary/10 text-primary/20 hover:bg-primary hover:text-white rounded-full
                    hover:cursor-pointer
                    "
        >
          {loading ? <Loader2 className="mr-2 h-4 w-4 animate-spin" /> : <ChevronRight />}
        </Button>
      )}
    </div>
  )
}
