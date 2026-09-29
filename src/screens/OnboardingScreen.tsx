import React, { useState } from 'react';
import { StatusBar, HomeIndicator } from '../components/PhoneShell';

interface Props {
  onDone: () => void;
}

const slides = [
  {
    gradient: 'linear-gradient(160deg, #EEF1FF 0%, #E8F3FF 100%)',
    accentColor: '#4F6AF0',
    lightAccent: '#EEF1FF',
    title: 'Borrow what\nyou need.',
    subtitle: 'Find items from students near you and borrow them for free. No cost, just community.',
    illustration: (
      <svg width="220" height="200" viewBox="0 0 220 200" fill="none">
        {/* Student figure */}
        <circle cx="110" cy="55" r="28" fill="#4F6AF0" fillOpacity="0.15" />
        <circle cx="110" cy="50" r="22" fill="#4F6AF0" fillOpacity="0.25" />
        <circle cx="110" cy="46" r="16" fill="#4F6AF0" />
        {/* Item box */}
        <rect x="70" y="100" width="80" height="60" rx="10" fill="white" stroke="#4F6AF0" strokeWidth="2" />
        <rect x="82" y="112" width="56" height="36" rx="6" fill="#EEF1FF" />
        <path d="M95 130H125M110 122L116 128L110 134" stroke="#4F6AF0" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" />
        {/* Calculator icon in box */}
        <rect x="88" y="118" width="24" height="24" rx="4" fill="#4F6AF0" fillOpacity="0.2" />
        <path d="M94 124H106M94 128H106M94 132H100M103 132H106" stroke="#4F6AF0" strokeWidth="1.5" strokeLinecap="round" />
        {/* Arrow down to student */}
        <path d="M110 90L110 100" stroke="#4F6AF0" strokeWidth="2" strokeDasharray="4 3" strokeLinecap="round" />
        <path d="M107 97L110 100L113 97" stroke="#4F6AF0" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" />
        {/* Stars */}
        <circle cx="55" cy="80" r="4" fill="#F5C842" fillOpacity="0.7" />
        <circle cx="165" cy="70" r="5" fill="#3BB8A2" fillOpacity="0.6" />
        <circle cx="40" cy="140" r="3" fill="#9585E0" fillOpacity="0.5" />
        <circle cx="180" cy="140" r="4" fill="#4F6AF0" fillOpacity="0.4" />
      </svg>
    ),
  },
  {
    gradient: 'linear-gradient(160deg, #E6F7F4 0%, #EEF1FF 100%)',
    accentColor: '#3BB8A2',
    lightAccent: '#E6F7F4',
    title: 'Share what\nyou have.',
    subtitle: 'List your items in seconds and help fellow students when they need it most.',
    illustration: (
      <svg width="220" height="200" viewBox="0 0 220 200" fill="none">
        {/* Phone with listing */}
        <rect x="75" y="30" width="70" height="120" rx="14" fill="white" stroke="#3BB8A2" strokeWidth="2" />
        <rect x="82" y="45" width="56" height="40" rx="6" fill="#E6F7F4" />
        <rect x="82" y="92" width="38" height="8" rx="4" fill="#3BB8A2" fillOpacity="0.3" />
        <rect x="82" y="106" width="28" height="6" rx="3" fill="#3BB8A2" fillOpacity="0.2" />
        <rect x="82" y="118" width="56" height="24" rx="6" fill="#3BB8A2" />
        <path d="M101 130H119M110 125L115 130L110 135" stroke="white" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" />
        {/* Item in box */}
        <rect x="90" y="50" width="40" height="30" rx="4" fill="white" />
        <path d="M100 65H120M110 58L114 63L110 68" stroke="#3BB8A2" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round" />
        {/* Upload arrows */}
        <path d="M55 80L75 70" stroke="#3BB8A2" strokeWidth="2" strokeDasharray="4 3" strokeLinecap="round" />
        <path d="M165 80L145 70" stroke="#3BB8A2" strokeWidth="2" strokeDasharray="4 3" strokeLinecap="round" />
        {/* Small item icons */}
        <rect x="30" y="70" width="28" height="28" rx="6" fill="#3BB8A2" fillOpacity="0.15" stroke="#3BB8A2" strokeWidth="1.5" />
        <path d="M39 84H49M44 79L47 83L44 87" stroke="#3BB8A2" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round" />
        <rect x="162" y="70" width="28" height="28" rx="6" fill="#3BB8A2" fillOpacity="0.15" stroke="#3BB8A2" strokeWidth="1.5" />
        <path d="M171 84H181M176 79L179 83L176 87" stroke="#3BB8A2" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round" />
        {/* Stars */}
        <circle cx="50" cy="130" r="4" fill="#F5C842" fillOpacity="0.7" />
        <circle cx="170" cy="130" r="5" fill="#4F6AF0" fillOpacity="0.5" />
      </svg>
    ),
  },
  {
    gradient: 'linear-gradient(160deg, #F0EDFC 0%, #FEF8E7 100%)',
    accentColor: '#9585E0',
    lightAccent: '#F0EDFC',
    title: 'Return with\nease.',
    subtitle: 'QR-verified handovers make borrowing safe and trustworthy for everyone.',
    illustration: (
      <svg width="220" height="200" viewBox="0 0 220 200" fill="none">
        {/* Checkmark circle */}
        <circle cx="110" cy="90" r="60" fill="#9585E0" fillOpacity="0.1" />
        <circle cx="110" cy="90" r="48" fill="#9585E0" fillOpacity="0.15" />
        <circle cx="110" cy="90" r="36" fill="#9585E0" />
        <path d="M97 90L107 100L124 82" stroke="white" strokeWidth="3" strokeLinecap="round" strokeLinejoin="round" />
        {/* QR dots around */}
        <rect x="55" y="40" width="20" height="20" rx="4" fill="#9585E0" fillOpacity="0.3" />
        <rect x="59" y="44" width="5" height="5" rx="1" fill="#9585E0" />
        <rect x="66" y="44" width="5" height="5" rx="1" fill="#9585E0" />
        <rect x="59" y="51" width="12" height="5" rx="1" fill="#9585E0" />
        <rect x="145" y="40" width="20" height="20" rx="4" fill="#F5C842" fillOpacity="0.5" />
        <rect x="149" y="44" width="5" height="5" rx="1" fill="#F5C842" />
        <rect x="156" y="44" width="5" height="5" rx="1" fill="#F5C842" />
        <rect x="149" y="51" width="12" height="5" rx="1" fill="#F5C842" />
        {/* Stars */}
        <circle cx="42" cy="100" r="5" fill="#F5C842" />
        <circle cx="178" cy="100" r="4" fill="#3BB8A2" fillOpacity="0.7" />
        <circle cx="80" cy="160" r="5" fill="#4F6AF0" fillOpacity="0.5" />
        <circle cx="140" cy="160" r="4" fill="#9585E0" fillOpacity="0.6" />
        {/* Return arrows */}
        <path d="M75 150C65 140 65 120 75 110" stroke="#9585E0" strokeWidth="2" strokeDasharray="4 3" strokeLinecap="round" />
        <path d="M145 150C155 140 155 120 145 110" stroke="#9585E0" strokeWidth="2" strokeDasharray="4 3" strokeLinecap="round" />
      </svg>
    ),
  },
];

export const OnboardingScreen = ({ onDone }: Props) => {
  const [step, setStep] = useState(0);
  const slide = slides[step];

  const next = () => {
    if (step < slides.length - 1) setStep(step + 1);
    else onDone();
  };

  return (
    <div className="flex flex-col h-full" style={{ background: slide.gradient, transition: 'background 0.4s ease' }}>
      <StatusBar />

      {/* Skip */}
      <div className="flex justify-end px-6 pt-2">
        {step < slides.length - 1 && (
          <button onClick={onDone} style={{ fontSize: 14, fontWeight: 600, color: slide.accentColor }}>
            Skip
          </button>
        )}
      </div>

      {/* Illustration */}
      <div className="flex-1 flex flex-col items-center justify-center px-8 gap-8">
        <div
          className="flex items-center justify-center"
          style={{
            width: 260,
            height: 220,
            borderRadius: 32,
            background: 'rgba(255,255,255,0.65)',
            backdropFilter: 'blur(8px)',
            boxShadow: '0 8px 32px rgba(0,0,0,0.06)',
          }}
        >
          {slide.illustration}
        </div>

        <div className="flex flex-col items-center gap-3 text-center">
          <h1 style={{ fontSize: 30, fontWeight: 800, color: '#1C2135', lineHeight: 1.2, whiteSpace: 'pre-line' }}>
            {slide.title}
          </h1>
          <p style={{ fontSize: 15, color: '#5A607A', fontWeight: 500, lineHeight: 1.6, maxWidth: 280 }}>
            {slide.subtitle}
          </p>
        </div>
      </div>

      {/* Dots + Button */}
      <div className="px-6 pb-4 flex flex-col gap-6">
        {/* Page dots */}
        <div className="flex justify-center gap-2">
          {slides.map((_, i) => (
            <div
              key={i}
              onClick={() => setStep(i)}
              className="rounded-full transition-all duration-300"
              style={{
                width: i === step ? 24 : 8,
                height: 8,
                background: i === step ? slide.accentColor : `${slide.accentColor}40`,
                cursor: 'pointer',
              }}
            />
          ))}
        </div>

        {/* Button */}
        <button
          onClick={next}
          className="w-full ripple"
          style={{
            height: 56,
            borderRadius: 18,
            background: `linear-gradient(135deg, ${slide.accentColor} 0%, ${slide.accentColor}CC 100%)`,
            color: 'white',
            fontSize: 16,
            fontWeight: 700,
            boxShadow: `0 8px 24px ${slide.accentColor}40`,
            border: 'none',
            cursor: 'pointer',
          }}
        >
          {step < slides.length - 1 ? 'Next →' : 'Get Started'}
        </button>
      </div>
      <HomeIndicator />
    </div>
  );
};
