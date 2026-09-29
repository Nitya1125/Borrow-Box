import React, { useState } from 'react';
import { StatusBar, HomeIndicator } from '../components/PhoneShell';
import { EyeIcon } from '../components/Icons';

interface Props {
  onLogin: () => void;
  onSignup: () => void;
}

export const LoginScreen = ({ onLogin, onSignup }: Props) => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [showPw, setShowPw] = useState(false);
  const [loading, setLoading] = useState(false);
  const [focused, setFocused] = useState<string | null>(null);

  const handleLogin = () => {
    setLoading(true);
    setTimeout(() => {
      setLoading(false);
      onLogin();
    }, 1200);
  };

  return (
    <div className="flex flex-col h-full" style={{ background: '#F4F6FF' }}>
      <StatusBar />

      {/* Header */}
      <div
        className="flex flex-col items-center justify-center pt-8 pb-10 px-6"
        style={{
          background: 'linear-gradient(160deg, #4F6AF0 0%, #7B61FF 100%)',
          borderBottomLeftRadius: 32,
          borderBottomRightRadius: 32,
        }}
      >
        {/* Logo */}
        <div
          className="flex items-center justify-center mb-4"
          style={{
            width: 64,
            height: 64,
            borderRadius: 20,
            background: 'rgba(255,255,255,0.2)',
            backdropFilter: 'blur(8px)',
            border: '1px solid rgba(255,255,255,0.3)',
          }}
        >
          <svg width="36" height="36" viewBox="0 0 48 48" fill="none">
            <rect x="8" y="18" width="32" height="22" rx="4" fill="white" fillOpacity="0.3" stroke="white" strokeWidth="1.8" />
            <path d="M8 24H40" stroke="white" strokeWidth="1.8" />
            <path d="M19 10C19 10 14 12.5 16 18M29 10C29 10 34 12.5 32 18" stroke="white" strokeWidth="1.8" strokeLinecap="round" />
            <path d="M24 8C21 8 19 10 19 12M24 8C27 8 29 10 29 12" stroke="white" strokeWidth="1.8" strokeLinecap="round" />
            <path d="M20 30H28M24 27L27 30L24 33" stroke="white" strokeWidth="1.8" strokeLinecap="round" strokeLinejoin="round" />
          </svg>
        </div>
        <h1 style={{ fontSize: 24, fontWeight: 800, color: 'white', margin: 0 }}>Welcome back</h1>
        <p style={{ fontSize: 14, color: 'rgba(255,255,255,0.75)', fontWeight: 500, margin: '4px 0 0' }}>
          Sign in to continue
        </p>
      </div>

      {/* Form */}
      <div className="flex-1 screen-scroll px-6 pt-8 pb-4">
        <div className="flex flex-col gap-4">
          {/* Email */}
          <div>
            <label style={{ fontSize: 12, fontWeight: 600, color: focused === 'email' ? '#4F6AF0' : '#5A607A', marginBottom: 6, display: 'block' }}>
              Email address
            </label>
            <input
              type="email"
              placeholder="you@college.edu"
              value={email}
              onChange={e => setEmail(e.target.value)}
              onFocus={() => setFocused('email')}
              onBlur={() => setFocused(null)}
              className="bb-input w-full"
              style={{
                height: 52,
                borderRadius: 14,
                border: `1.5px solid ${focused === 'email' ? '#4F6AF0' : '#E5E8F2'}`,
                background: 'white',
                padding: '0 16px',
                fontSize: 15,
                fontWeight: 500,
                color: '#1C2135',
                transition: 'border-color 0.2s ease, box-shadow 0.2s ease',
                boxShadow: focused === 'email' ? '0 0 0 3px rgba(79, 106, 240, 0.10)' : 'var(--bb-shadow-sm)',
              }}
            />
          </div>

          {/* Password */}
          <div>
            <label style={{ fontSize: 12, fontWeight: 600, color: focused === 'pw' ? '#4F6AF0' : '#5A607A', marginBottom: 6, display: 'block' }}>
              Password
            </label>
            <div className="relative">
              <input
                type={showPw ? 'text' : 'password'}
                placeholder="Your password"
                value={password}
                onChange={e => setPassword(e.target.value)}
                onFocus={() => setFocused('pw')}
                onBlur={() => setFocused(null)}
                className="bb-input w-full pr-12"
                style={{
                  height: 52,
                  borderRadius: 14,
                  border: `1.5px solid ${focused === 'pw' ? '#4F6AF0' : '#E5E8F2'}`,
                  background: 'white',
                  padding: '0 48px 0 16px',
                  fontSize: 15,
                  fontWeight: 500,
                  color: '#1C2135',
                  transition: 'border-color 0.2s ease, box-shadow 0.2s ease',
                  boxShadow: focused === 'pw' ? '0 0 0 3px rgba(79, 106, 240, 0.10)' : 'var(--bb-shadow-sm)',
                }}
              />
              <button
                onClick={() => setShowPw(!showPw)}
                className="absolute right-4 top-1/2 -translate-y-1/2"
                style={{ color: '#9BA3B8' }}
              >
                <EyeIcon size={20} closed={!showPw} />
              </button>
            </div>
          </div>

          {/* Forgot password */}
          <div className="flex justify-end">
            <button style={{ fontSize: 13, fontWeight: 600, color: '#4F6AF0' }}>
              Forgot password?
            </button>
          </div>

          {/* Login button */}
          <button
            onClick={handleLogin}
            className="ripple"
            style={{
              marginTop: 8,
              height: 56,
              borderRadius: 18,
              background: loading ? '#9BA3B8' : 'linear-gradient(135deg, #4F6AF0 0%, #7B61FF 100%)',
              color: 'white',
              fontSize: 16,
              fontWeight: 700,
              border: 'none',
              cursor: loading ? 'not-allowed' : 'pointer',
              boxShadow: loading ? 'none' : '0 8px 24px rgba(79, 106, 240, 0.35)',
              transition: 'all 0.3s ease',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              gap: 8,
            }}
          >
            {loading ? (
              <>
                <div
                  style={{
                    width: 20,
                    height: 20,
                    border: '2px solid rgba(255,255,255,0.4)',
                    borderTopColor: 'white',
                    borderRadius: '50%',
                    animation: 'spin 0.8s linear infinite',
                  }}
                />
                Signing in...
              </>
            ) : (
              'Sign In'
            )}
          </button>

          {/* Divider */}
          <div className="flex items-center gap-3 my-2">
            <div style={{ flex: 1, height: 1, background: '#E5E8F2' }} />
            <span style={{ fontSize: 13, color: '#9BA3B8', fontWeight: 500 }}>or continue with</span>
            <div style={{ flex: 1, height: 1, background: '#E5E8F2' }} />
          </div>

          {/* Google button */}
          <button
            style={{
              height: 52,
              borderRadius: 14,
              border: '1.5px solid #E5E8F2',
              background: 'white',
              fontSize: 15,
              fontWeight: 600,
              color: '#1C2135',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              gap: 10,
              boxShadow: 'var(--bb-shadow-sm)',
              cursor: 'pointer',
            }}
          >
            <svg width="20" height="20" viewBox="0 0 20 20" fill="none">
              <path d="M18.1 10.2C18.1 9.55 18.04 8.92 17.92 8.31H10V11.9H14.6C14.38 13.05 13.74 14.03 12.78 14.69V17.04H15.56C17.16 15.56 18.1 13.07 18.1 10.2Z" fill="#4285F4" />
              <path d="M10 19.25C12.4 19.25 14.42 18.47 15.56 17.04L12.78 14.69C11.99 15.23 10.99 15.55 10 15.55C7.67 15.55 5.7 14.06 4.99 12H2.11V14.43C3.26 16.74 6.43 19.25 10 19.25Z" fill="#34A853" />
              <path d="M4.99 12C4.82 11.46 4.73 10.89 4.73 10.31C4.73 9.73 4.82 9.16 4.99 8.62V6.19H2.11C1.4 7.64 1 9.25 1 10.93C1 12.61 1.4 14.22 2.11 15.67L4.99 12Z" fill="#FBBC05" />
              <path d="M10 5.07C11.1 5.07 12.09 5.45 12.88 6.2L15.65 3.43C14.41 2.26 12.4 1.5 10 1.5C6.43 1.5 3.26 4.01 2.11 6.32L4.99 8.75C5.7 6.69 7.67 5.07 10 5.07Z" fill="#EA4335" />
            </svg>
            Sign in with Google
          </button>
        </div>
      </div>

      {/* Signup link */}
      <div className="flex justify-center items-center gap-1 pb-4">
        <span style={{ fontSize: 14, color: '#9BA3B8', fontWeight: 500 }}>Don't have an account?</span>
        <button onClick={onSignup} style={{ fontSize: 14, fontWeight: 700, color: '#4F6AF0' }}>Sign Up</button>
      </div>
      <HomeIndicator />

      <style>{`
        @keyframes spin { to { transform: rotate(360deg); } }
      `}</style>
    </div>
  );
};
