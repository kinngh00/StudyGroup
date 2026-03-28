import { Outlet } from "react-router-dom";
import { Header } from "@/components/organisms/Header";
import { Sidebar } from "@/components/organisms/Sidebar";
import { Footer } from "@/components/organisms/Footer";

export const MainLayout = () => (
  <div className="min-h-screen">
    <Header />
    <main className="mx-auto grid max-w-7xl gap-5 px-4 py-6 lg:grid-cols-[270px_1fr]">
      <Sidebar />
      <section>
        <Outlet />
      </section>
    </main>
    <Footer />
  </div>
);
