"use client"

import { useState, useEffect } from "react"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Textarea } from "@/components/ui/textarea"
import { BotMessageSquare, ChevronLeft, ChevronRight, Loader2, Trash } from "lucide-react"
import { isToday } from "date-fns"
import { toast } from "sonner"
import { useRouter } from "next/navigation"

import { type DiaryDTO, type CreateDiaryRequestDTO } from "@/types/dto"
import { formatDate, formatDateToYmd } from "@/lib/date"
import { useDiaryByDate, useCreateDiary } from "../queries"
import DiaryCalendarDialog from "./DiaryCalendarDialog"
import { Card, CardAction, CardContent, CardFooter, CardHeader } from "@/components/ui/card"
import { Skeleton } from "@/components/ui/skeleton"
import Link from "next/link"
import DiaryDeleteDialog from "./DiaryDeleteDialog"

interface DiaryContentProps {
  initialDate: string
  initialDiary?: DiaryDTO
}

export default function DiaryContent({ initialDate, initialDiary }: DiaryContentProps) {
  const router = useRouter()
  const [selectedDate, setSelectedDate] = useState(initialDate)
  const [calendarOpen, setCalendarOpen] = useState(false)

  // Form state
  const [title, setTitle] = useState(initialDiary?.title || "")
  const [content, setContent] = useState(initialDiary?.content || "")
  const [isSubmitting, setIsSubmitting] = useState(false)

  // React Query로 선택된 날짜의 데이터 관리
  const {
    data: diary,
    isLoading,
    error
  } = useDiaryByDate(selectedDate, {
    initialData: selectedDate === initialDate ? initialDiary : undefined,
    enabled: !!selectedDate && selectedDate.length === 10 // YYYY-MM-DD 형식인 경우에만 활성화
  })

  // 날짜가 바뀔 때마다 diary 데이터에 따라 편집 모드 결정
  useEffect(() => {
    if (selectedDate !== initialDate) {
      // 다이어리가 없거나 에러인 경우 편집 모드, 있으면 읽기 모드
      const hasDiary = diary && !error
      if (hasDiary) {
        setTitle(diary.title)
        setContent(diary.content)
      }
    }
  }, [diary, error, selectedDate, initialDate])

  // 일기 생성 mutation
  const createDiaryMutation = useCreateDiary({
    onSuccess: (data) => {
      toast.success("일기가 저장되었습니다.")
      setTitle(data.title)
      setContent(data.content)
      // URL 업데이트 (페이지 이동 없이)
      router.replace(`/diaries/date/${selectedDate}`, { scroll: false })
    },
    onError: (error) => {
      toast.error("일기 저장에 실패했습니다.")
      console.error("일기 저장 실패:", error)
    },
    onSettled: () => {
      setIsSubmitting(false)
    }
  })

  // 날짜 변경 처리
  const handleDateChange = (newDate: string) => {
    setSelectedDate(newDate)
    // 폼 상태 초기화
    setTitle("")
    setContent("")
    // URL 업데이트
    router.replace(`/diaries/date/${newDate}`, { scroll: false })
  }

  // 이전/다음 날짜 이동
  const handlePrevDate = () => {
    const prevDate = new Date(selectedDate)
    prevDate.setDate(prevDate.getDate() - 1)
    handleDateChange(prevDate.toISOString().split("T")[0])
  }

  const handleNextDate = () => {
    const nextDate = new Date(selectedDate)
    nextDate.setDate(nextDate.getDate() + 1)
    handleDateChange(nextDate.toISOString().split("T")[0])
  }

  // 이전/다음 날짜 prefetch
  useEffect(() => {
    const prevDate = new Date(selectedDate)
    prevDate.setDate(prevDate.getDate() - 1)
    const prevDateStr = prevDate.toISOString().split("T")[0]

    const nextDate = new Date(selectedDate)
    nextDate.setDate(nextDate.getDate() + 1)
    const nextDateStr = nextDate.toISOString().split("T")[0]

    // 이전/다음 날짜 페이지 미리 로드
    router.prefetch(`/diaries/date/${prevDateStr}`)
    if (!isToday(nextDateStr)) {
      router.prefetch(`/diaries/date/${nextDateStr}`)
    }
  }, [selectedDate, router])

  // 달력에서 날짜 선택
  const handleCalendarDateSelect = (date?: Date) => {
    if (!date) return
    const ymd = formatDateToYmd(date)
    handleDateChange(ymd)
    setCalendarOpen(false)
  }

  // 저장 처리
  const handleSave = () => {
    if (!title.trim()) {
      toast.error("제목을 입력해주세요.")
      return
    }
    if (!content.trim()) {
      toast.error("내용을 입력해주세요.")
      return
    }

    setIsSubmitting(true)
    const createData: CreateDiaryRequestDTO = {
      title: title.trim(),
      content: content.trim(),
      diaryDate: selectedDate
    }
    createDiaryMutation.mutate(createData)
  }

  const isPending = createDiaryMutation.isPending || isSubmitting
  const currentDiary = diary && !error ? diary : selectedDate === initialDate ? initialDiary : null

  return (
    <Card className="space-y-4">
      <CardHeader className="block">
        <CardAction className="w-full mx-auto flex items-center gap-3 justify-center">
          <Button size="icon" variant="ghost" onClick={handlePrevDate}>
            <ChevronLeft />
          </Button>

          <DiaryCalendarDialog
            open={calendarOpen}
            onOpenChange={setCalendarOpen}
            trigger={
              <Button variant="ghost" className="justify-between font-medium">
                <span>{formatDateToYmd(selectedDate)}</span>
              </Button>
            }
            onDateSelect={handleCalendarDateSelect}
          />

          <Button size="icon" variant="ghost" disabled={isToday(selectedDate)} onClick={handleNextDate}>
            <ChevronRight />
          </Button>
        </CardAction>
      </CardHeader>

      <CardContent className="flex-1">
        {isLoading ? (
          selectedDate !== initialDate && (
            <div className="space-y-4">
              <div className="space-y-2">
                <Skeleton className="w-full" bold />
                <Skeleton className="w-1/4" />
              </div>
              <div className="space-y-2">
                <Skeleton className="w-full" />
                <Skeleton className="w-full" />
                <Skeleton className="w-2/3" />
              </div>
            </div>
          )
        ) : (
          <>
            {currentDiary ? (
              // 읽기 모드 (DiaryView)
              <div className="space-y-4">
                <section>
                  <h1 className="text-lg font-bold">{currentDiary.title}</h1>
                  <div className="text-muted-foreground">
                    <time className="text-sm">{formatDate(currentDiary.createdAt)} 작성됨</time>
                  </div>

                  <div className="mt-2 h-full whitespace-pre-wrap">{currentDiary.content}</div>
                </section>
              </div>
            ) : (
              // 생성 모드 (DiaryForm)
              <div className="space-y-4">
                <Input
                  value={title}
                  onChange={(e) => setTitle(e.target.value)}
                  placeholder="제목을 입력하세요"
                  disabled={isPending}
                />

                <Textarea
                  value={content}
                  onChange={(e) => setContent(e.target.value)}
                  placeholder="오늘의 일기를 작성해보세요!"
                  disabled={isPending}
                  className="min-h-[300px] resize-none"
                />
              </div>
            )}
          </>
        )}
      </CardContent>

      <CardFooter>
        {currentDiary ? (
          <div className="w-full flex items-center justify-between">
            <Button asChild variant="secondary" className="rounded-full">
              <Link href={`/diaries/${initialDiary?.id || currentDiary.id}/analysis`}>
                <BotMessageSquare className="stroke-2" />
                <span>페르소나 분석</span>
              </Link>
            </Button>
            <DiaryDeleteDialog
              id={currentDiary.id}
              trigger={
                <Button variant="ghost" className="rounded-full hover:text-error hover:bg-error/5">
                  <Trash className="stroke-2" />
                </Button>
              }
            />
          </div>
        ) : (
          !isLoading && (
            <Button size="long" onClick={handleSave} disabled={isPending} className="w-full">
              {isPending ? (
                <>
                  <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                  저장 중...
                </>
              ) : (
                "저장"
              )}
            </Button>
          )
        )}
      </CardFooter>
    </Card>
  )
}
