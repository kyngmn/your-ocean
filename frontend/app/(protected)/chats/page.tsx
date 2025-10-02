import ChatForm from "@/features/chats/ui/ChatMessageForm"
import { ChatOptimisticProvider } from "@/features/chats/context/ChatOptimisticContext"
import ChatsView from "@/features/chats/ui/ChatsView"
import Header from "@/components/layout/Header"

export const dynamic = "force-dynamic"

export default function ChatPage() {
  return (
    <>
      <Header title="채팅" />

      <ChatOptimisticProvider>
        <main className="page min-h-screen has-header relative">
          <section className="flex-1 flex">
            <ChatsView />
          </section>

          <div className="sticky bottom-2 mx-auto w-full max-w-md sm:max-w-lg">
            <ChatForm />
          </div>
        </main>
      </ChatOptimisticProvider>
    </>
  )
}
