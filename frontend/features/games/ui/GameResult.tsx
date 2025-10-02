"use client"

import { Button } from "../../../components/ui/button";
import { Card, CardContent, CardFooter, CardHeader, CardTitle } from "../../../components/ui/card";
import Typography from "../../../components/ui/Typography";

interface ResultItem {
    resultTitle: string;
    result: string;
}

interface GameResultProps {
    results: ResultItem[];
    buttonVariant?: "default" | "outline" | "secondary" | "ghost" | "link";
    buttonText?: string;
    buttonOnClick?: () => void;
}

export default function GameResult({results, buttonVariant="default", buttonText="확인", buttonOnClick }: GameResultProps) {
    return  <>
    <div className="text-center my-4">
        <CardHeader>
            <CardTitle>
                <Typography type="h3">
                    결과
                </Typography>
            </CardTitle>
        </CardHeader>
    </div>
        <div className="flex justify-center items-center">
        <Card className="w-full text-center">
            <CardContent className="flex flex-col items-center justify-center py-8 space-y-16">
                {results.map((item, index) => (
                    <div key={index} className="w-full">
                        <Typography type="h3" >
                            {item.resultTitle}
                        </Typography>
                        <Typography type="p">
                            {item.result}
                        </Typography>
                    </div>
                ))}
            </CardContent>
            <CardFooter>
                <Button variant={buttonVariant} size="long" onClick={buttonOnClick}>
                    {buttonText}
                </Button>
            </CardFooter>
        </Card>
        </div>
    </>
}