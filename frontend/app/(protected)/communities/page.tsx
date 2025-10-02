import Header from "@/components/layout/Header";
import Navbar from "@/components/layout/Navbar";
import FriendsList from "@/features/communities/ui/FriendList";

export default function CommunityPage() {
  return (
    <>
    <Header title="친구 목록"/>
    <main className="page has-header has-bottom-nav">
      <div className=" section">
        <FriendsList />
      </div>
    </main>
    <Navbar />
    </>
  );
}