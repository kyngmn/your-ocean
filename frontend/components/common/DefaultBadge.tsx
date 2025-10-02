import { Badge } from "../ui/badge";
import Typography from "../ui/Typography";

interface DefaultBadgeProps {
    variant: "outline" | "default" | "secondary" | "destructive" ;
    text: string;
}

export default function DefaultBadge({ variant, text }: DefaultBadgeProps) {
    return (
        <div className="flex justify-center items-center p-4">
            <Badge variant={variant} className="rounded-full border-black p-3"><Typography type="h4">{text}</Typography></Badge>
        </div>
    )
}