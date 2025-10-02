import Image from "next/image";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import Typography from "@/components/ui/Typography";
import BigFiveProgressBar from "./BigFiveProgressBar";
import { Label } from "@/components/ui/label";

interface PersonaCardProps {
  indicator: string;
  bigfiveName: string;
  bigfive?: string;
  src?: string;
  value?: number;
  description?: string;
  high?: string;
  low?: string;
}

export default function PersonaCard({
  indicator,
  bigfiveName,
  bigfive,
  src,
  value = 0,
  description,
  high,
  low,
}: PersonaCardProps) {
  return (
    <div className="flex justify-center items-center">
      <Card className="w-full text-center">
        <CardHeader>
          <CardTitle>
            <Typography type="h3">{bigfiveName}</Typography>
          </CardTitle>
          <Typography type="h4">{bigfive}</Typography>
          <BigFiveProgressBar
            value={value}
            indicator={indicator as "O" | "C" | "E" | "A" | "N"}
          />
        </CardHeader>
        <CardContent className="flex flex-col items-start justify-start gap-4">
          {src && (
            <div className="w-full flex justify-center">
              <Image
                src={src}
                alt={bigfiveName}
                width={0}
                height={0}
                sizes="100vw"
                className="w-1/2 h-1/2"
              />
            </div>
          )}
          <div className="flex flex-col gap-4">
          <div className="w-full">
            <Label className="text-left block mb-2 font-bold border-b border-gray-300 pb-2">설명</Label>
            <Typography type="p" className="text-left">{description}</Typography>
          </div>
          <div className="w-full">
            <Label className="text-left block mb-2 font-bold border-b border-gray-300 pb-2">이 수치가 높은 사람은?</Label>
            <Typography type="p" className="text-left">{high}</Typography>
          </div>
          <div className="w-full">
            <Label className="text-left block mb-2 font-bold border-b border-gray-300 pb-2">이 수치가 낮은 사람은?</Label>
            <Typography type="p" className="text-left">{low}</Typography>
          </div>
          </div>
        </CardContent>
      </Card>
    </div>
  );
}
