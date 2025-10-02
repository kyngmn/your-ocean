import DiaryCalendarDialog from "@/features/diaries/ui/DiaryCalendarDialog"
import DiaryContent from "@/features/diaries/ui/DiaryContent"
import Header from "@/components/layout/Header"
import Navbar from "@/components/layout/Navbar"
import { getDiary } from "@/app/actions/diaries"
import { notFound } from "next/navigation"

export const dynamic = "force-dynamic"

export default async function DiaryByIdPage({ params }: { params: Promise<{ id: string }> }) {
  const { id } = await params
  const diaryId = parseInt(id, 10)

  // 날짜 유효성 검증
  if (isNaN(diaryId) || diaryId < 0) {
    notFound()
  }

  // 해당 날짜의 일기 조회
  const data = await getDiary(diaryId)

  // 데이터가 없거나 에러인 경우 404 처리
  if (!data.isSuccess || !data.result) {
    notFound()
  }

  // 해당 날짜의 일기 찾기
  const diary = data.result

  return (
    <>
      <Header title="일기" rightSlot={<DiaryCalendarDialog />} />
      <main className="page px-0 sm:px-4 has-header has-bottom-nav">
        <DiaryContent initialDate={diary.diaryDate} initialDiary={diary} />
      </main>
      <Navbar />
    </>
  )
}
