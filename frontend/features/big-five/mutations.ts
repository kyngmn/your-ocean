import { useMutation, useQueryClient } from "@tanstack/react-query"

import { CompleteSurveyRequestDTO } from "@/types/dto"
import { completeSurvey } from "@/app/actions/surveys"
import { handleServerAction } from "@/hooks/useServerActions"

// 설문 제출
export const useMutationSurveys = () => {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (responses: CompleteSurveyRequestDTO["responses"]) => handleServerAction(completeSurvey(responses)),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["surveys"] })
    }
  })
}
