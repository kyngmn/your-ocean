"use client"

import Image from "next/image";
import { Card, CardContent, CardFooter, CardHeader, CardTitle } from "../ui/card";
import Typography from "@/components/ui/Typography";
import LottiePlayer from "@/components/common/LottiePlayer";
import { Button } from "@/components/ui/button";

interface CardSectionProps {
    title: string;
    subtitle?: string;
    isLottie?: boolean;
    src?: string;
    isAnimate?: string;
    description?: string | React.ReactNode;
    buttonVariant?: "default" | "outline" | "secondary" | "ghost" | "link";
    buttonText?: string;
    buttonOnClick?: () => void;
    children?: React.ReactNode;
    type?: "default" | "gameExplain";
}

export default function CardSection({ title, subtitle, isLottie=false, src, description, buttonVariant="default", buttonText, buttonOnClick, children, type="default" ,isAnimate=""}: CardSectionProps) {
    return <div className="flex justify-center items-center">
        <Card className="w-full text-center">
            <CardHeader>                                                           
                <CardTitle>
                    <Typography type="h3">
                        {title}
                    </Typography>
                </CardTitle>
                <Typography type="h4">
                    {subtitle}
                </Typography>
            </CardHeader>
            {children && (
                <CardContent className="flex flex-col items-center justify-center">
                    {children}
                </CardContent>
            )}
            {!children && (
                <CardContent className={`flex flex-col items-center justify-center ${type === "gameExplain" ? " py-10 min-h-[400px]" : ""}`}>
                {isLottie && src && <div className="w-full h-full"> 
                <LottiePlayer src={src} loop={true} autoplay={true}/>
                </div>}
                {!isLottie && src && <Image src={src} alt={title} width={0} height={0} sizes="100vw" className={`w-1/2 h-1/2 ${isAnimate}`}  />}
                <Typography type="p">
                    {description}
                </Typography>
            </CardContent>
            )}
            <CardFooter>
                {buttonText && (
                    <Button variant={buttonVariant} size="long" onClick={buttonOnClick}>
                        {buttonText}
                    </Button>
                )} 
            </CardFooter>
        </Card>
    </div>;
}