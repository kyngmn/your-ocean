import DiaryCalendarDialog from "@/features/diaries/ui/DiaryCalendarDialog"
import DiaryContent from "@/features/diaries/ui/DiaryContent"
import Header from "@/components/layout/Header"
import Navbar from "@/components/layout/Navbar"
import { getDiaryByDate } from "@/app/actions/diaries"
import { notFound } from "next/navigation"

export default async function DiaryByDatePage({ params }: { params: Promise<{ date: string }> }) {
  const { date } = await params

  // 날짜 형식 검증 (YYYY-MM-DD)
  const dateRegex = /^\d{4}-\d{2}-\d{2}$/
  if (!dateRegex.test(date)) {
    notFound()
  }

  // 날짜 유효성 검증
  const parsedDate = new Date(date + "T00:00:00")
  if (isNaN(parsedDate.getTime())) {
    notFound()
  }

  // 해당 날짜의 일기 조회
  const result = await getDiaryByDate(date)

  // 해당 날짜의 일기 찾기
  const diary = result.result

  return (
    <>
      <Header title="일기" rightSlot={<DiaryCalendarDialog />} />
      <main className="page px-0 sm:px-4 has-header has-bottom-nav">
        <section className="py-4">
          <DiaryContent initialDate={date} initialDiary={diary} />
        </section>
      </main>
      <Navbar />
    </>
  )
}
