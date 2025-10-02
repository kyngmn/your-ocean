import { type DiaryAnalysisDTO } from "@/types/dto"
import { Button } from "@/components/ui/button"
import Link from "next/link"
import { MoveLeft } from "lucide-react"
import { BIG5_INDEX_LABELS_KO, BIG5_INDEX_VALUES, getBig5IndexLabel } from "@/types/enums"
import PersonaAvatar from "@/components/common/PersonaAvatar"
import MessageMotion from "./MessageMotion"
import { Card, CardContent, CardTitle } from "@/components/ui/card"
import { cn } from "@/lib/utils"
import { Badge } from "@/components/ui/badge"

interface Props {
  diaryAnalysis: DiaryAnalysisDTO
}

export default function DiaryAnalysisView({ diaryAnalysis }: Props) {
  const { diaryId, oceanMessages, summary } = diaryAnalysis
  return (
    <div className="space-y-12">
      {summary?.domainClassification && (
        <div className="flex flex-col items-center mb-3">
          <PersonaAvatar
            personality={summary.domainClassification.toLocaleUpperCase() as (typeof BIG5_INDEX_VALUES)[number]}
            size="lg"
          />
        </div>
      )}

      <Card className="p-6">
        <CardContent className="space-y-6 px-0">
          {summary?.domainClassification && (
            <div className="flex flex-col items-center justify-center space-y-2 text-center font-bold text-xl">
              <span>{getBig5IndexLabel(summary?.domainClassification)}</span>
              <Badge variant="outline">이번 일기의 주된 성격</Badge>
            </div>
          )}

          <div>
            {summary?.keywords && (
              <div className="flex items-center justify-center gap-2">
                {summary?.keywords.map((keyword) => (
                  <span
                    key={keyword}
                    className={cn(
                      summary?.domainClassification === "AGREEABLENESS"
                        ? "bg-big-five-A/10 text-big-five-A-dark"
                        : summary?.domainClassification === "OPENNESS"
                          ? "bg-big-five-O/10 text-big-five-O-dark"
                          : summary?.domainClassification === "CONSCIENTIOUSNESS"
                            ? "bg-big-five-C/10 text-big-five-C-dark"
                            : summary?.domainClassification === "NEUROTICISM"
                              ? "bg-big-five-N/10 text-big-five-N-dark"
                              : "bg-big-five-E/10 text-big-five-E-dark",
                      "px-2 py-1.5 text-sm font-medium rounded-md"
                    )}
                  >
                    #{keyword}
                  </span>
                ))}
              </div>
            )}
          </div>

          <div className="font-mono rounded-lg">{summary?.finalConclusion}</div>
        </CardContent>
      </Card>

      <Card className="p-6">
        <CardContent className="space-y-6 px-0">
          <div className="text-center font-bold text-xl">Big 5 점수</div>
          <div className="space-y-3">
            {summary?.big5Scores && (
              <div className="flex items-center justify-around gap-1">
                {BIG5_INDEX_VALUES.map((index) => {
                  const scoreKey = index.toLowerCase() as keyof typeof summary.big5Scores
                  return (
                    <div
                      key={index}
                      className={cn(
                        index === "AGREEABLENESS"
                          ? "bg-big-five-A/10"
                          : index === "OPENNESS"
                            ? "bg-big-five-O/10"
                            : index === "CONSCIENTIOUSNESS"
                              ? "bg-big-five-C/10"
                              : index === "NEUROTICISM"
                                ? "bg-big-five-N/10"
                                : "bg-big-five-E/10",
                        "flex flex-col items-center flex-1 py-3 rounded-xl font-mono space-y-2"
                      )}
                    >
                      <div
                        className={cn(
                          index === "AGREEABLENESS"
                            ? "text-big-five-A-dark"
                            : index === "OPENNESS"
                              ? "text-big-five-O-dark"
                              : index === "CONSCIENTIOUSNESS"
                                ? "text-big-five-C-dark"
                                : index === "NEUROTICISM"
                                  ? "text-big-five-N-dark"
                                  : "text-big-five-E-dark",
                          "text-sm font-bold"
                        )}
                      >
                        {BIG5_INDEX_LABELS_KO[index]}
                      </div>
                      <div className="">
                        {summary?.big5Scores?.[scoreKey] || 0}
                        <span className="leading-loose text-sm font-medium text-left text-muted-foreground">/1</span>
                      </div>
                    </div>
                  )
                })}
              </div>
            )}
          </div>
        </CardContent>
      </Card>

      <Card className="p-6">
        <CardTitle className="text-lg">성격 분석 결과</CardTitle>
        <CardContent className="space-y-6 px-0">
          {oceanMessages?.map((item) => (
            <div key={item.id} className="space-y-3 rounded-lg w-full">
              <div className="flex items-center gap-2">
                {item?.personality && <PersonaAvatar personality={item.personality} size="sm" showBorder />}
                <p className="font-semibold">{getBig5IndexLabel(item?.personality)}</p>
              </div>
              <div className="flex items-center gap-2 ml-12">
                <MessageMotion chars={item?.message?.split("")} />
              </div>
            </div>
          ))}
        </CardContent>
      </Card>
      <div className="flex items-center justify-between">
        <Button asChild variant="outline">
          <Link href={`/diaries/${diaryId}`} className="flex items-center gap-1">
            <MoveLeft />
            <span>일기로</span>
          </Link>
        </Button>
        {/* <div className="text-right text-muted-foreground">
          <time className="text-sm">{formatDateToYmd(createdAt)}</time>
        </div> */}
      </div>
    </div>
  )
}
