import React, { useState } from 'react';
import { StatusBar, HomeIndicator } from '../components/PhoneShell';
import { BackIcon, EyeIcon } from '../components/Icons';

interface Props {
  onDone: () => void;
  onBack: () => void;
}

const getStrength = (pw: string): { score: number; label: string; color: string } => {
  let score = 0;
  if (pw.length >= 8) score++;
  if (/[A-Z]/.test(pw)) score++;
  if (/[0-9]/.test(pw)) score++;
  if (/[^A-Za-z0-9]/.test(pw)) score++;
  const labels = ['', 'Weak', 'Fair', 'Good', 'Strong'];
  const colors = ['', '#FF7A7A', '#F5C842', '#3BB8A2', '#4F6AF0'];
  return { score, label: labels[score] || '', color: colors[score] || '' };
};

export const SignupScreen = ({ onDone, onBack }: Props) => {
  const [form, setForm] = useState({ name: '', email: '', password: '', confirm: '', college: '' });
  const [showPw, setShowPw] = useState(false);
  const [agreed, setAgreed] = useState(false);
  const [loading, setLoading] = useState(false);
  const [focused, setFocused] = useState<string | null>(null);
  const strength = getStrength(form.password);

  const update = (k: string, v: string) => setForm(f => ({ ...f, [k]: v }));

  const handleCreate = () => {
    if (!agreed) return;
    setLoading(true);
    setTimeout(() => { setLoading(false); onDone(); }, 1400);
  };

  const fields = [
    { key: 'name', label: 'Full Name', placeholder: 'Nitya Kapoor', type: 'text' },
    { key: 'email', label: 'College Email', placeholder: 'you@college.edu', type: 'email' },
    { key: 'password', label: 'Password', placeholder: 'Min. 8 characters', type: showPw ? 'text' : 'password', hasToggle: true },
    { key: 'confirm', label: 'Confirm Password', placeholder: 'Re-enter password', type: 'password' },
    { key: 'college', label: 'College / Institution', placeholder: 'e.g. BITS Pilani', type: 'text' },
  ];

  return (
    <div className="flex flex-col h-full" style={{ background: '#F4F6FF' }}>
      <StatusBar />

      {/* Header */}
      <div className="flex items-center gap-4 px-6 pt-2 pb-4">
        <button onClick={onBack} className="flex items-center justify-center" style={{ width: 40, height: 40, borderRadius: 12, background: 'white', boxShadow: 'var(--bb-shadow-sm)' }}>
          <BackIcon size={20} color="#1C2135" />
        </button>
        <div>
          <h1 style={{ fontSize: 22, fontWeight: 800, color: '#1C2135', margin: 0 }}>Create Account</h1>
          <p style={{ fontSize: 13, color: '#9BA3B8', fontWeight: 500, margin: 0 }}>Join your campus community</p>
        </div>
      </div>

      <div className="flex-1 screen-scroll px-6 pb-4">
        <div className="flex flex-col gap-4">
          {fields.map(({ key, label, placeholder, type, hasToggle }) => (
            <div key={key}>
              <label style={{ fontSize: 12, fontWeight: 600, color: focused === key ? '#4F6AF0' : '#5A607A', marginBottom: 6, display: 'block' }}>
                {label}
              </label>
              <div className="relative">
                <input
                  type={type}
                  placeholder={placeholder}
                  value={(form as Record<string, string>)[key]}
                  onChange={e => update(key, e.target.value)}
                  onFocus={() => setFocused(key)}
                  onBlur={() => setFocused(null)}
                  className="bb-input w-full"
                  style={{
                    height: 52,
                    borderRadius: 14,
                    border: `1.5px solid ${focused === key ? '#4F6AF0' : key === 'confirm' && form.confirm && form.confirm !== form.password ? '#FF7A7A' : '#E5E8F2'}`,
                    background: 'white',
                    padding: `0 ${hasToggle ? '48px' : '16px'} 0 16px`,
                    fontSize: 15,
                    fontWeight: 500,
                    color: '#1C2135',
                    transition: 'all 0.2s ease',
                    boxShadow: focused === key ? '0 0 0 3px rgba(79,106,240,0.10)' : 'var(--bb-shadow-sm)',
                  }}
                />
                {hasToggle && (
                  <button onClick={() => setShowPw(!showPw)} className="absolute right-4 top-1/2 -translate-y-1/2" style={{ color: '#9BA3B8' }}>
                    <EyeIcon size={20} closed={!showPw} />
                  </button>
                )}
              </div>

              {/* Password strength */}
              {key === 'password' && form.password.length > 0 && (
                <div className="mt-2">
                  <div className="flex gap-1 mb-1">
                    {[1, 2, 3, 4].map(i => (
                      <div key={i} className="flex-1 rounded-full strength-bar" style={{ height: 4, background: i <= strength.score ? strength.color : '#E5E8F2' }} />
                    ))}
                  </div>
                  <span style={{ fontSize: 11, fontWeight: 600, color: strength.color }}>{strength.label}</span>
                </div>
              )}

              {/* Confirm mismatch */}
              {key === 'confirm' && form.confirm && form.confirm !== form.password && (
                <p style={{ fontSize: 11, color: '#FF7A7A', fontWeight: 500, marginTop: 4 }}>Passwords don't match</p>
              )}
            </div>
          ))}

          {/* Terms */}
          <div className="flex items-start gap-3 mt-2">
            <div
              onClick={() => setAgreed(!agreed)}
              className="flex-shrink-0 flex items-center justify-center"
              style={{
                width: 22,
                height: 22,
                borderRadius: 6,
                border: `2px solid ${agreed ? '#4F6AF0' : '#E5E8F2'}`,
                background: agreed ? '#4F6AF0' : 'white',
                cursor: 'pointer',
                marginTop: 1,
                transition: 'all 0.2s ease',
              }}
            >
              {agreed && (
                <svg width="12" height="12" viewBox="0 0 12 12" fill="none">
                  <path d="M2 6L5 9L10 3" stroke="white" strokeWidth="1.8" strokeLinecap="round" strokeLinejoin="round" />
                </svg>
              )}
            </div>
            <p style={{ fontSize: 13, color: '#5A607A', lineHeight: 1.5 }}>
              I agree to the{' '}
              <span style={{ color: '#4F6AF0', fontWeight: 600 }}>Terms & Conditions</span>
              {' '}and{' '}
              <span style={{ color: '#4F6AF0', fontWeight: 600 }}>Privacy Policy</span>
            </p>
          </div>

          {/* Button */}
          <button
            onClick={handleCreate}
            className="ripple"
            style={{
              marginTop: 8,
              height: 56,
              borderRadius: 18,
              background: !agreed ? '#E5E8F2' : loading ? '#9BA3B8' : 'linear-gradient(135deg, #4F6AF0 0%, #7B61FF 100%)',
              color: !agreed ? '#9BA3B8' : 'white',
              fontSize: 16,
              fontWeight: 700,
              border: 'none',
              cursor: !agreed ? 'not-allowed' : 'pointer',
              boxShadow: agreed && !loading ? '0 8px 24px rgba(79,106,240,0.35)' : 'none',
              transition: 'all 0.3s ease',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              gap: 8,
            }}
          >
            {loading ? (
              <>
                <div style={{ width: 20, height: 20, border: '2px solid rgba(255,255,255,0.4)', borderTopColor: 'white', borderRadius: '50%', animation: 'spin 0.8s linear infinite' }} />
                Creating account...
              </>
            ) : (
              'Create Account'
            )}
          </button>

          {/* Login link */}
          <div className="flex justify-center items-center gap-1 pb-2">
            <span style={{ fontSize: 14, color: '#9BA3B8', fontWeight: 500 }}>Already have an account?</span>
            <button onClick={onBack} style={{ fontSize: 14, fontWeight: 700, color: '#4F6AF0' }}>Sign In</button>
          </div>
        </div>
      </div>
      <HomeIndicator />

      <style>{`@keyframes spin { to { transform: rotate(360deg); } }`}</style>
    </div>
  );
};
