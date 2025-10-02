import { SurveyRequestDTO } from "@/types/dto"
import { getSurveys } from "@/app/actions/surveys"
import { handleServerAction } from "@/hooks/useServerActions"
import { useQuery } from "@tanstack/react-query"

// 설문 문항 조회
export const useSurveys = (page: SurveyRequestDTO) => {
  return useQuery({
    queryKey: ["surveys", page.page],
    queryFn: () => handleServerAction(getSurveys(page)),
    staleTime: 5 * 60 * 1000 // 5분
  })
}
