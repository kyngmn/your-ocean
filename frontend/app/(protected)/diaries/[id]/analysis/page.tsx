import { Alert, AlertDescription, AlertTitle } from "@/components/ui/alert"

import { AlertCircleIcon } from "lucide-react"
import DiaryAnalysisView from "@/features/diaries/ui/DiaryAnalysisView"
import DiaryCalendarDialog from "@/features/diaries/ui/DiaryCalendarDialog"
import Header from "@/components/layout/Header"
import { getDiaryAnalysis } from "@/app/actions/diaries"
import { notFound } from "next/navigation"

export default async function DiaryAnalysisPage({ params }: { params: Promise<{ id: string }> }) {
  const { id } = await params
  const diaryId = parseInt(id, 10)

  // 날짜 유효성 검증
  if (isNaN(diaryId) || diaryId < 0) {
    notFound()
  }

  // 해당 날짜의 일기 분석 내용 조회
  const data = await getDiaryAnalysis(diaryId)
  const diaryAnalysis = data.result

  return (
    <>
      <Header title="일기" rightSlot={<DiaryCalendarDialog />} />
      <main className="page px-0 sm:px-4 has-header has-bottom-nav">
        <section className="py-4">
          {diaryAnalysis ? (
            <DiaryAnalysisView diaryAnalysis={diaryAnalysis} />
          ) : (
            <Alert>
              <AlertCircleIcon />
              <AlertTitle>준비중</AlertTitle>
              <AlertDescription>
                <p>아직 분석이 나오지 않았어요</p>
              </AlertDescription>
            </Alert>
          )}
        </section>
      </main>
    </>
  )
}
