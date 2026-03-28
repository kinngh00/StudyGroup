import { Link, useNavigate } from "react-router-dom";
import { Avatar } from "@/components/atoms/Avatar";
import { Button } from "@/components/atoms/Button";
import { useAppDispatch, useAppSelector } from "@/app/hooks";
import { logout } from "@/features/auth/authSlice";

export const Header = () => {
  const { isAuthenticated, user } = useAppSelector((state) => state.auth);
  const navigate = useNavigate();
  const dispatch = useAppDispatch();

  return (
    <header className="sticky top-0 z-20 border-b border-slate-200/70 bg-white/80 backdrop-blur">
      <div className="mx-auto flex max-w-7xl items-center justify-between px-4 py-3">
        <Link className="text-xl font-extrabold tracking-tight text-slate-900" to="/">
          StudyGroup
        </Link>
        <div className="flex items-center gap-3">
          {isAuthenticated && user ? (
            <>
              <Avatar name={user.name} src={user.profileImageUrl} />
              <Button
                variant="ghost"
                onClick={() => {
                  dispatch(logout());
                  navigate("/");
                }}
              >
                로그아웃
              </Button>
            </>
          ) : (
            <>
              <Button variant="secondary" onClick={() => navigate("/login")}>
                로그인
              </Button>
              <Button onClick={() => navigate("/signup")}>회원가입</Button>
            </>
          )}
        </div>
      </div>
    </header>
  );
};
