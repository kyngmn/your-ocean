import { Tabs, TabsContent, TabsList, TabsTrigger } from "@radix-ui/react-tabs";

interface MenuTabsProps {
  TabTitle1: string;
  TabTitle2: string;
  TabTitle3?: string;
  children1: React.ReactNode;
  children2: React.ReactNode;
  children3?: React.ReactNode;
  disabledTab2?: boolean;
}

export default function MenuTabs({
  TabTitle1,
  TabTitle2,
  TabTitle3,
  children1,
  children2,
  children3,
  disabledTab2 = false,
}: MenuTabsProps) {
  return (
    <Tabs className="w-full" defaultValue={TabTitle1}>
      <TabsList className="w-full flex bg-transparent p-0">
        <TabsTrigger
          value={TabTitle1}
          className="flex-1 bg-transparent p-4 text-center text-gray-500 data-[state=active]:border-b-2 data-[state=active]:border-black data-[state=active]:text-black data-[state=active]:bg-transparent border-b-2 border-transparent"
        >
          {TabTitle1}
        </TabsTrigger>
        <TabsTrigger
          value={TabTitle2}
          disabled={disabledTab2}
          className={`flex-1 bg-transparent p-4 text-center border-b-2 border-transparent ${
            disabledTab2 
              ? "text-gray-300 cursor-not-allowed" 
              : "text-gray-500 data-[state=active]:border-b-2 data-[state=active]:border-black data-[state=active]:text-black data-[state=active]:bg-transparent"
          }`}
        >
          {TabTitle2}
        </TabsTrigger>
        {TabTitle3 && (
          <TabsTrigger
            value={TabTitle3}
            className="flex-1 bg-transparent p-4 text-center text-gray-500 data-[state=active]:border-b-2 data-[state=active]:border-black data-[state=active]:text-black data-[state=active]:bg-transparent border-b-2 border-transparent"
          >
            {TabTitle3}
          </TabsTrigger>
        )}
      </TabsList>
      <TabsContent value={TabTitle1} className="mt-4">
        {children1}
      </TabsContent>
      <TabsContent value={TabTitle2} className="mt-4">
        {children2}
      </TabsContent>
      {TabTitle3 && (
      <TabsContent value={TabTitle3} className="mt-4">
        {children3}
      </TabsContent>
      )}
    </Tabs>
  );
}
