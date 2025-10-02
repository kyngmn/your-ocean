import { Progress } from "../ui/progress";
import Typography from "../ui/Typography";

interface ProgressBarProps {
    type?: "oneLine" | "twoLine";
    title?: string;
    percent?: string;
    value: number;
    entireRound?: number;
    currentRound?: number;
}

export default function ProgressBar({ type, title, percent, value, entireRound, currentRound }: ProgressBarProps) {
    return (
        <div>
            {type === "oneLine" && (
                <div className="flex justify-center items-center gap-2 ">
                    <Progress value={value} />
                    <Typography type="small" className="whitespace-nowrap">{percent}</Typography>
                </div>
            )}
            {type === "twoLine" && (
                <div className="flex flex-col gap-2 text-center">
                    <Typography type="h3">{title}</Typography>
                    <Typography type="p"> 라운드 {currentRound}/{entireRound}</Typography>
                    <Progress value={value} />
                </div>
            )}

        </div>
    )};