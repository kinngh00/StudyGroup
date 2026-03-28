import { Link } from "react-router-dom";

export const NotFoundPage = () => (
  <div className="panel p-10 text-center">
    <h1 className="text-3xl font-bold">404</h1>
    <p className="mt-2 text-slate-600">요청하신 페이지를 찾을 수 없습니다.</p>
    <Link className="mt-4 inline-block text-brand-700 underline" to="/">
      메인으로 이동
    </Link>
  </div>
);
