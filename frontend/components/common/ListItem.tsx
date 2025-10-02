import { ReactNode } from "react";

interface ListItemProps {
  items: ReactNode[];
  direction?: "row" | "col";
  className?: string;
}

export default function ListItem({ 
  items, 
  className = "",
  direction = "row",
}: ListItemProps) {
  return (
    <div className={className + " flex flex-" + direction + " gap-2 flex-wrap"}>
      {items.map((item, index) => (
        <div key={index}>
          {item}
        </div>
      ))}
    </div>
  );
}