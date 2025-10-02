"use client"

import { formatDateToYmd, getThisYearMonth } from "@/lib/date"

import { Calendar } from "@/components/ui/calendar"
import { Circle } from "lucide-react"
import LoadingSpinner from "@/components/common/LoadingSpinner"
import { cn } from "@/lib/utils"
import { useDiaryCalendar } from "../queries"
import { useRouter } from "next/navigation"
import { useState } from "react"

interface DiaryCalendarProps {
  diaryDate?: Date
  onDateSelect?: (date: Date | undefined) => void
  getHref?: (date: string) => string
  className?: string
  refetch?: boolean
}

export default function DiaryCalendar({ diaryDate, onDateSelect, getHref, className, refetch }: DiaryCalendarProps) {
  const router = useRouter()
  const [selected, setSelected] = useState<Date | undefined>(diaryDate)

  const thisYearMonth = getThisYearMonth()
  const { data, isFetching } = useDiaryCalendar(thisYearMonth, {
    enabled: refetch // refetch 옵션 true일 때
    // refetchOnMount: refetch  // markedDates 없을 때만 마운트 시 새로고침
  })

  // 일기가 작성된 날짜인지 확인
  const isMarkedDate = (date: Date) => {
    const dateStr = formatDateToYmd(date)
    const markedDates = data?.diaryDates || []
    return markedDates.includes(dateStr)
  }

  // 날짜 선택 시 처리
  const handleSelect = (date: Date | undefined) => {
    if (date && date > new Date()) {
      // 미래 날짜 선택 방지
      return
    }
    setSelected(date)
    if (onDateSelect) {
      onDateSelect(date)
    } else if (date) {
      // 기본 동작: 해당 날짜의 일기 페이지로 이동
      const dateStr = formatDateToYmd(date)
      router.push(getHref ? getHref?.(dateStr) : `/diaries/date/${dateStr}`)
    }
  }

  if (isFetching) {
    return <LoadingSpinner size="sm" />
  }

  return (
    <Calendar
      mode="single"
      selected={selected}
      onSelect={handleSelect}
      className={cn("w-full aspect-square self-center", className)}
      disabled={{ after: new Date() }}
      // hidden={{ after: new Date() }}
      modifiers={{
        marked: (date) => isMarkedDate(date)
      }}
      // modifiersClassNames={{
      //   marked: "diary-marked-date"
      // }}
      components={{
        DayButton: ({ day, children, disabled, ...props }) => {
          const isMarked = isMarkedDate(day.date)
          return (
            <button
              {...props}
              className={cn(
                isMarked ? "" : "",
                disabled ? "" : "cursor-pointer",
                "relative w-full h-full flex items-center justify-center p-2"
              )}
            >
              <span>{children}</span>
              {!disabled && isMarked && (
                <Circle
                  size={6}
                  className="absolute top-1 sm:top-2 left-1/2 -translate-x-1/2 fill-green-500 stroke-green-400"
                />
              )}
            </button>
          )
        }
      }}
    />
  )
}
