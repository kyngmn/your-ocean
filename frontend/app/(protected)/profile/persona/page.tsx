"use client"

import ListItem from "@/components/common/ListItem";
import Header from "@/components/layout/Header";
import { useUserPersonas } from "@/features/profile/queries";
import PersonaCard from "@/features/profile/ui/PersonaCard";
import { dev } from "@/lib/dev";


export default function PersonaPage() {

  const { data: userPersonas } = useUserPersonas()

  dev.log("🔥 userPersonas", userPersonas)

  const bigfive = [
    {
      key: "O",
      indicator: "O",
      bigfiveName: "개방밍",
      bigfive: "Openness",
      image: "/characters/O.png",
      value: userPersonas?.userO,
      description:
        "나는 새로운 경험과 아이디어를 즐겨. 익숙하지 않은 환경에서도 호기심이 먼저 생겨!",
      high: "호기심이 많고 창의적인 생각이 활발하고 새로운 도전을 즐겨요",
      low: "익숙한 방식을 더 선호하고 변화를 부담스럽게 느껴요",
    },
    {
      key: "C",
      indicator: "C",
      bigfiveName: "성실밍",
      bigfive: "Conscientiousness",
      image: "/characters/C.png",
      value: userPersonas?.userC,
      description:
        "나는 체계적이고 계획적인 걸 선호해. 안정적으로 목표를 향해 나아가는 게 나한테 맞아",
      high: "계획을 꼼꼼히 세우고 작은 일도 성실하게 끝까지 지켜요",
      low: "즉흥적으로 움직이고 일을 미루거나 대충 처리하기 쉬워요",
    },
    {
      key: "E", 
      indicator: "E",
      bigfiveName: "외향밍",
      bigfive: "Extraversion",
      image: "/characters/E.png",
      value: userPersonas?.userE,
      description:
        "나는 다른 사람들과 함께할 때 에너지를 얻어. 자연스럽게 분위기를 이끌어가고 싶어 해!",
      high: "활발해서 주변 교류를 많이 하고 적극적으로 새로운 만남이나 활동을 제안해요",
      low: "혼자 있는 걸 더 선호하고 타인과의 교류를 피하려고 해요",
    },
    {
      key: "A",
      indicator: "A",
      bigfiveName: "친화밍",
      bigfive: "Agreeableness",
      image: "/characters/A.png",
      value: userPersonas?.userA,
      description:
        "나는 다 같이 잘 지내는 게 제일 중요해. 평화가 최고야. 다른 사람 마음을 잘 파악하는 게 특기야!",
      high: "배려심이 강하고 타인의 의견을 존중하며 잘 맞춰요",
      low: "주로 경쟁적이거나 타인의 요구보다 자기 이익을 더 중요시 해요",
    },
    {
      key: "N",
      indicator: "N",
      bigfiveName: "신경밍",
      bigfive: "Neuroticism",
      image: "/characters/N.png",
      value: userPersonas?.userN,
      description:
        "나는 감정의 변화에 민감해. 상황에 따라 쉽게 불안해지기도 하고, 반대로 들뜨기도 해",
      high: "사소한 일에도 불안이나 걱정을 크게 느끼고 예민하게 반응해요",
      low: "차분하고 안정적이라 웬만한 일은 크게 흔들리지 않아요",
    },
  ];

  const bigfiveCards = bigfive.map((item) => (
    <PersonaCard key={item.key} indicator={item.indicator} bigfiveName={item.bigfiveName} bigfive={item.bigfive} src={item.image} value={item.value} description={item.description} high={item.high} low={item.low} />
  ));

  return (
    <>
      <Header type="back" title="나의 페르소나" />
      <main className="page has-header has-bottom-nav">
        <div className="section flex-1 flex flex-col items-center justify-center gap-4">
          <ListItem items={bigfiveCards} direction="col" />
        </div>
      </main>
    </>
  );
}
