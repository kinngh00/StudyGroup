import { useEffect, useRef } from "react";

declare global {
  interface Window {
    google?: {
      accounts: {
        id: {
          initialize: (config: {
            client_id: string;
            callback: (response: { credential?: string }) => void;
          }) => void;
          renderButton: (
            parent: HTMLElement,
            options: {
              theme?: "outline" | "filled_blue" | "filled_black";
              size?: "large" | "medium" | "small";
              width?: number;
              text?: "signin_with" | "signup_with" | "continue_with" | "signin";
              shape?: "rectangular" | "pill" | "circle" | "square";
            }
          ) => void;
          prompt: () => void;
          cancel: () => void;
        };
      };
    };
  }
}

interface GoogleIdTokenButtonProps {
  buttonText?: "signin_with" | "signup_with" | "continue_with" | "signin";
  onSuccess: (idToken: string) => void;
}

const GOOGLE_SCRIPT_ID = "google-identity-client";
const GOOGLE_SCRIPT_SRC = "https://accounts.google.com/gsi/client";

export const GoogleIdTokenButton = ({ buttonText = "continue_with", onSuccess }: GoogleIdTokenButtonProps) => {
  const buttonContainerRef = useRef<HTMLDivElement | null>(null);

  useEffect(() => {
    const clientId = import.meta.env.VITE_GOOGLE_CLIENT_ID;
    if (!clientId) {
      return;
    }

    const renderGoogleButton = () => {
      if (!window.google || !buttonContainerRef.current) {
        return;
      }

      buttonContainerRef.current.innerHTML = "";
      window.google.accounts.id.initialize({
        client_id: clientId,
        callback: (response) => {
          if (response.credential) {
            onSuccess(response.credential);
          }
        }
      });

      window.google.accounts.id.renderButton(buttonContainerRef.current, {
        theme: "outline",
        size: "large",
        width: 320,
        text: buttonText,
        shape: "rectangular"
      });
    };

    const existingScript = document.getElementById(GOOGLE_SCRIPT_ID) as HTMLScriptElement | null;
    if (existingScript) {
      if (window.google) {
        renderGoogleButton();
      } else {
        existingScript.addEventListener("load", renderGoogleButton, { once: true });
      }
      return;
    }

    const googleScript = document.createElement("script");
    googleScript.id = GOOGLE_SCRIPT_ID;
    googleScript.src = GOOGLE_SCRIPT_SRC;
    googleScript.async = true;
    googleScript.defer = true;
    googleScript.onload = renderGoogleButton;
    document.head.appendChild(googleScript);

    return () => {
      if (window.google) {
        window.google.accounts.id.cancel();
      }
    };
  }, [buttonText, onSuccess]);

  if (!import.meta.env.VITE_GOOGLE_CLIENT_ID) {
    return <p className="text-xs text-slate-500">Google 로그인 비활성화: VITE_GOOGLE_CLIENT_ID 값이 없습니다.</p>;
  }

  return <div ref={buttonContainerRef} />;
};
