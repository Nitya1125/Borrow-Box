import React, { useEffect } from 'react';
import { StatusBar } from '../components/PhoneShell';
import { BorrowBoxLogo } from '../components/Icons';

interface Props {
  onDone: () => void;
}

export const SplashScreen = ({ onDone }: Props) => {
  useEffect(() => {
    const t = setTimeout(onDone, 2200);
    return () => clearTimeout(t);
  }, [onDone]);

  return (
    <div
      className="flex flex-col h-full"
      style={{
        background: 'linear-gradient(160deg, #4F6AF0 0%, #6B5AE8 50%, #7B61FF 100%)',
      }}
    >
      <StatusBar light />
      <div className="flex-1 flex flex-col items-center justify-center gap-6">
        <div className="splash-logo flex flex-col items-center gap-5">
          {/* Logo */}
          <div
            className="flex items-center justify-center"
            style={{
              width: 96,
              height: 96,
              borderRadius: 28,
              background: 'rgba(255,255,255,0.18)',
              backdropFilter: 'blur(12px)',
              border: '1px solid rgba(255,255,255,0.3)',
            }}
          >
            <svg width="56" height="56" viewBox="0 0 48 48" fill="none">
              {/* Box body */}
              <rect x="8" y="18" width="32" height="22" rx="4" fill="white" fillOpacity="0.25" stroke="white" strokeWidth="1.8" />
              {/* Box top */}
              <path d="M8 24H40" stroke="white" strokeWidth="1.8" />
              {/* Circular arrows */}
              <path
                d="M19 10C19 10 14 12.5 16 18M29 10C29 10 34 12.5 32 18"
                stroke="white" strokeWidth="1.8" strokeLinecap="round"
              />
              <path d="M24 8C21 8 19 10 19 12M24 8C27 8 29 10 29 12" stroke="white" strokeWidth="1.8" strokeLinecap="round" />
              {/* Arrows on box */}
              <path d="M20 30H28M24 27L27 30L24 33" stroke="white" strokeWidth="1.8" strokeLinecap="round" strokeLinejoin="round" />
            </svg>
          </div>

          <div className="flex flex-col items-center gap-1.5">
            <span style={{ fontSize: 32, fontWeight: 800, color: 'white', letterSpacing: -0.5 }}>
              BorrowBox
            </span>
          </div>
        </div>

        <div className="splash-text flex flex-col items-center gap-2">
          <span style={{ fontSize: 15, color: 'rgba(255,255,255,0.75)', fontWeight: 500, letterSpacing: 2 }}>
            BORROW · SHARE · RETURN
          </span>
        </div>
      </div>

      {/* Bottom loading indicator */}
      <div className="splash-text flex justify-center pb-12">
        <div style={{ display: 'flex', gap: 6 }}>
          {[0, 1, 2].map(i => (
            <div
              key={i}
              className="rounded-full"
              style={{
                width: i === 0 ? 24 : 8,
                height: 8,
                background: 'rgba(255,255,255,0.5)',
                animation: `pulse 1.2s ease ${i * 0.2}s infinite`,
              }}
            />
          ))}
        </div>
      </div>
    </div>
  );
};
