"use client";

import React, {useState, useMemo, useEffect } from "react";
import LabelCard from "@/components/common/LabelCard";
import Image from "next/image";
import CardSection from "@/components/common/CardSection";
import BigFive from "./BigFive";
import DefaultBadge from "@/components/common/DefaultBadge";
import Typography from "@/components/ui/Typography";
import SmallCodeChart from "./SmallCodeChart";
import { Button } from "@/components/ui/button";
import { PersonaCode } from "@/types/enums";
import { Badge } from "@/components/ui/badge";
import { useSelfReport } from "../queries";
import Loading from "@/components/common/Loading";
import { useReportStore } from "@/stores/report";

export default function TestReport() {
  const [selectedOcean, setSelectedOcean] = useState<PersonaCode>('O');
  const { data: selfReport, isLoading, isError, error } = useSelfReport();
  const { setSelfReportBigFive } = useReportStore();

  // 안전하게 JSON 파싱 (문자열이면 parse, 객체면 그대로 사용) - 메모이제이션
  const parsedContent = useMemo(() => {
    return selfReport?.content
      ? (typeof selfReport.content === "string"
          ? JSON.parse(selfReport.content)
          : selfReport.content) as { bigFiveScores: Record<string, number> }
      : null;
  }, [selfReport?.content]);
  
  // 빅파이브 점수 계산 - 메모이제이션
  const bigFiveScores = useMemo(() => {
    if (parsedContent?.bigFiveScores) {
      const { calculateBigFiveScores } = useReportStore.getState();
      return calculateBigFiveScores(parsedContent.bigFiveScores);
    }
    return null;
  }, [parsedContent?.bigFiveScores]);

  // 스토어에 빅파이브 점수 저장
  useEffect(() => {
    if (bigFiveScores) {
      setSelfReportBigFive(parsedContent?.bigFiveScores || {});
    }
  }, [bigFiveScores, setSelfReportBigFive, parsedContent?.bigFiveScores]);

  if (isLoading) return <Loading />;
  if (isError) throw error;


  // OCEAN 타입별 설정 (버튼용)
  const oceanButtons = [
    { key: 'O' as PersonaCode, name: '개방성' },
    { key: 'C' as PersonaCode, name: '성실성' },
    { key: 'E' as PersonaCode, name: '외향성' },
    { key: 'A' as PersonaCode, name: '친화성' },
    { key: 'N' as PersonaCode, name: '신경성' }
  ];

  // 데이터 준비 전 가드
  if (!bigFiveScores) return <Loading />;

  // 실제 데이터에서 가장 높은 BigFive 수치 찾기
  const maxTrait = Object.entries(bigFiveScores).reduce((max, [trait, value]) => 
    value > max.value ? { trait, value } : max, 
    { trait: 'O', value: 0 }
  );

    // BigFive 코드에 따른 캐릭터 매칭
  const getCharacterInfo = (trait: string) => {
    const characterMap = {
      'O': { name: '개방밍', image: '/characters/O.png' },
      'C': { name: '성실밍', image: '/characters/C.png' },
      'E': { name: '외향밍', image: '/characters/E.png' },
      'A': { name: '친화밍', image: '/characters/A.png' },
      'N': { name: '신경밍', image: '/characters/N.png' }
    };
    return characterMap[trait as keyof typeof characterMap] || { name: '개방밍', image: '/characters/O.png' };
  };

  const characterInfo = getCharacterInfo(maxTrait.trait);

  return (
    <>
      <section className="section space-y-4">
        <LabelCard
          title={`검사 완료일: ${selfReport?.createdAt ? new Date(selfReport.createdAt).toISOString().split("T")[0] : "날짜 없음"}`}
        />

        <div className="text-center mt-8 mb-0">
          <Badge variant="outline">대표캐릭터</Badge>
          <Typography type="h4">{characterInfo.name}</Typography>
          </div>
        <div className="flex justify-center w-full">
          <Image src={characterInfo.image} alt={characterInfo.name} width={300} height={300} />
        </div>

        <div className="text-center">
          <Typography type="h3">BIG5 종합 결과</Typography>
          <DefaultBadge variant="outline" text="신뢰도 80%" />
          <CardSection title="">
            <BigFive
              values={bigFiveScores}
            />
          </CardSection>
        </div>
        <div className="text-center">
          <Typography type="h3">상세 지표</Typography>
          
          {/* OCEAN 선택 버튼 그룹 */}
          <div className="flex flex-wrap gap-2 justify-center mb-4 py-4">
            {oceanButtons.map(({ key, name }) => (
              <Button
                key={key}
                variant={selectedOcean === key ? "default" : "outline"}
                size="sm"
                onClick={() => setSelectedOcean(key)}
                className="min-w-20"
              >
                {name}
              </Button>
            ))}
          </div>

          <CardSection title="">
            <SmallCodeChart 
              smallCodes={parsedContent?.bigFiveScores || {}}
              selectedOcean={selectedOcean}
            />
          </CardSection>
        </div>
      </section>
    </>
  );
}
