"use client"

import { type DiaryDTO } from "@/types/dto"
import { Dialog, DialogContent, DialogDescription, DialogTitle, DialogTrigger } from "@/components/ui/dialog"
import { Button } from "@/components/ui/button"
import { useState } from "react"
import DiaryCalendar from "./DiaryCalendar"
import { Calendar } from "lucide-react"

interface Props extends Partial<Pick<DiaryDTO, "id" | "title" | "content" | "diaryDate">> {
  trigger?: React.ReactNode
  onDateSelect?: (date: Date | undefined) => void
  open?: boolean
  onOpenChange?(open: boolean): void
  getHref?: (date: string) => string
}

export default function DiaryCalendarDialog({
  trigger,
  onDateSelect,
  open: externalOpen,
  onOpenChange,
  getHref
}: Props) {
  // 내부 상태와 외부 상태 통합 관리
  const [internalOpen, setInternalOpen] = useState(false)
  const isOpen = externalOpen ?? internalOpen

  const handleOpenChange = (open: boolean) => {
    if (onOpenChange) {
      onOpenChange(open)
    } else {
      setInternalOpen(open)
    }
  }

  return (
    <Dialog open={isOpen} onOpenChange={handleOpenChange}>
      <DialogTrigger asChild>
        {trigger || (
          <Button variant="ghost">
            <Calendar />
          </Button>
        )}
      </DialogTrigger>
      <DialogContent aria-describedby="diary-calendar">
        <DialogTitle>
          <div className="flex items-center gap-3">
            <span>달력</span>
          </div>
        </DialogTitle>
        <DialogDescription>날짜를 선택하세요</DialogDescription>
        <DiaryCalendar onDateSelect={onDateSelect} getHref={getHref} refetch={isOpen} />
      </DialogContent>
    </Dialog>
  )
}
