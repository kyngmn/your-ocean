"use client"

import BigFiveTest from "@/features/big-five/ui/BigFiveTest"
import { Button } from "@/components/ui/button"
import ListItem from "@/components/common/ListItem"
import Loading from "@/components/common/Loading"
import ProgressBar from "@/components/common/ProgressBar"
import { dev } from "@/lib/dev"
import { toast } from "sonner"
import { useMutationSurveys } from "@/features/big-five/mutations"
import { useRouter } from "next/navigation"
import { useState } from "react"
import { useSurveys } from "@/features/big-five/queries"

export default function ProgressPage() {
  const router = useRouter()
  const [currentPage, setCurrentPage] = useState(1)
  const [allResponses, setAllResponses] = useState<Array<{ surveyId: number; value: number }>>([])

  const TOTAL_PAGES = 24 // 120 문항 / 5개씩 = 24페이지

  // 설문 문항 불러오기 (현재 페이지)
  const { data: surveys, isLoading, isError, error } = useSurveys({ page: currentPage })

  // 설문 제출 mutation
  const submitSurveyMutation = useMutationSurveys()

  // 로딩 상태 처리
  if (isLoading) {
    return (
      <>
        <Loading />
      </>
    )
  }

  // 에러 상태 처리
  if (isError) {
    throw error
  }

  // 데이터 없음 처리
  if (!surveys) return <div>데이터가 없습니다</div>

  const progress = (currentPage / TOTAL_PAGES) * 100

  // 응답 저장 - 전체 응답 배열에 추가/업데이트
  const handleResponseChange = async (surveyId: number, value: number) => {
    setAllResponses((prev) => {
      // 기존 응답이 있는지 확인
      const existingIndex = prev.findIndex((response) => response.surveyId === surveyId)

      if (existingIndex !== -1) {
        // 기존 응답 업데이트
        const updated = [...prev]
        updated[existingIndex] = { surveyId, value }
        return updated
      } else {
        // 새로운 응답 추가
        return [...prev, { surveyId, value }]
      }
    })

    dev.log("응답 저장", { surveyId, value })
  }

  // 현재 페이지의 모든 문항이 응답되었는지 확인
  const isCurrentPageComplete = () => {
    if (!surveys) return false

    // 현재 페이지의 모든 문항 ID 가져오기
    const currentPageSurveyIds = surveys.map((survey) => survey.id)

    // 현재 페이지의 모든 문항에 대한 응답이 있는지 확인
    return currentPageSurveyIds.every((surveyId) => allResponses.some((response) => response.surveyId === surveyId))
  }

  // 다음 페이지로 이동
  const handleNext = () => {
    // 현재 페이지의 모든 문항이 응답되었는지 확인
    if (!isCurrentPageComplete()) {
      toast.error("모든 문항에 응답해주세요.")
      return
    }

    if (currentPage < TOTAL_PAGES) {
      setCurrentPage((prev) => prev + 1)
    }
  }

  // 이전 페이지로 이동
  const handlePrevious = () => {
    if (currentPage > 1) {
      setCurrentPage((prev) => prev - 1)
    }
  }

  // 설문 완료 - 모든 응답을 API로 제출
  const handleComplete = async () => {
    // 현재 페이지의 모든 문항이 응답되었는지 확인
    if (!isCurrentPageComplete()) {
      toast.error("모든 문항에 응답해주세요.")
      return
    }

    // 전체 120개 문항이 모두 응답되었는지 확인 (24페이지 * 5문항 = 120문항)
    if (allResponses.length < 120) {
      toast.error("모든 문항에 응답해주세요.")
      return
    }

    try {
      dev.log("설문 완료 - 총 응답 수:", allResponses.length)
      dev.log("모든 응답:", allResponses)
      dev.log("mutation 상태:", { isPending: submitSurveyMutation.isPending, isError: submitSurveyMutation.isError })

      // mutation을 사용하여 설문 제출
      const result = await submitSurveyMutation.mutateAsync(allResponses)

      dev.log("mutation 결과:", result)

      if (result.isSuccess) {
        dev.log("설문 제출 성공:", result.data)
        goResult()
      } else {
        dev.error("설문 제출 실패:", result.error)
        toast.error("설문 제출에 실패했습니다. 다시 시도해주세요.")
      }
    } catch (error) {
      dev.error("설문 제출 오류:", error)
      dev.error("mutation error:", submitSurveyMutation.error)
      toast.error("설문 제출 중 오류가 발생했습니다. 다시 시도해주세요.")
    }
  }

  const goResult = () => {
    router.push("/big-five/result")
  }

  return (
    <>
      <main className="page flex flex-col">
        {/* 진행 바 영역 - 위쪽 고정 */}
        <div className="flex-shrink-0 p-4 mt-8">
          <ProgressBar type="oneLine" value={Math.round(progress)} percent={`${Math.round(progress)}%`} />

          <div className="mt-2 text-center">
            <span className="text-sm text-gray-600">
              {currentPage} / {TOTAL_PAGES} 페이지
            </span>
          </div>
        </div>

        {/* 설문 영역 - 가운데 꽉차게 */}
        <div className="flex-1 flex flex-col justify-center px-4">
          <ListItem
            direction="col"
            items={surveys?.map((item) => {
              // 현재 응답 값 찾기
              const currentResponse = allResponses.find((response) => response.surveyId === item.id)
              return (
                <BigFiveTest
                  question={item.questionText}
                  key={item.id}
                  selectedValue={currentResponse?.value}
                  onValueChange={(value) => handleResponseChange(item.id, value)}
                />
              )
            })}
            className="space-y-4"
          />
        </div>

        {/* 버튼 영역 - 아래쪽 고정 */}
        <div className="flex-shrink-0 p-4 mb-4">
          <div className="flex justify-between">
            <Button variant="outline" size="lg" onClick={handlePrevious} disabled={currentPage === 1}>
              이전
            </Button>

            {currentPage === TOTAL_PAGES ? (
              <Button size="lg" onClick={handleComplete} disabled={submitSurveyMutation.isPending}>
                {submitSurveyMutation.isPending ? "제출 중" : "설문 완료"}
              </Button>
            ) : (
              <Button size="lg" onClick={handleNext}>
                다음
              </Button>
            )}
          </div>
        </div>
      </main>
    </>
  )
}
