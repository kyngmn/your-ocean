"use client"

import { Card,CardContent } from "../ui/card";
import Typography from "../ui/Typography";

interface LabelCardProps {
    title: string;
    children?: React.ReactNode;
}

export default function LabelCard({ title, children}: LabelCardProps) {
    return <div className="flex justify-center items-center">
        <Card className="w-full text-center">
            <CardContent>                                                           
                    <Typography type="h3">
                        {title}
                    </Typography>
                    {children}
            </CardContent>
        </Card>
    </div>;
}