"use client";

import MenuTabs from "@/components/common/MenuTabs";
import Header from "@/components/layout/Header";
import { useFinalReport } from "@/features/profile/queries";
import BehaviorTestReport from "@/features/profile/ui/BehaviorTestReport";
import TestReport from "@/features/profile/ui/TestReport";

export default function ReportPage() {


  const { data: finalReport } = useFinalReport()
  

  return (
    <>
      <Header title="리포트 보기" type="back" />
      <main className="page has-header has-bottom-nav mb-10">
        <MenuTabs 
          TabTitle1="설문 결과" 
          TabTitle2="행동 분석" 
          children1={<TestReport/>} 
          children2={<BehaviorTestReport/>} 
          disabledTab2={!finalReport}
        />
      </main>
    </>
  );
}
