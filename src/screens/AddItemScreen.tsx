import React, { useState } from 'react';
import { HomeIndicator } from '../components/PhoneShell';
import { BackIcon, CameraIcon, CheckIcon } from '../components/Icons';
import { categories } from '../data';

interface Props {
  onBack: () => void;
  onDone: () => void;
}

const steps = ['Photo', 'Details', 'Preferences', 'Preview'];
const handoverMethods = ['Library gate', 'Hostel room', 'Canteen area', 'Academic block', 'Sports complex'];
const durations = ['1 day', '2 days', '3 days', '5 days', '7 days', '2 weeks'];

export const AddItemScreen = ({ onBack, onDone }: Props) => {
  const [step, setStep] = useState(0);
  const [photo, setPhoto] = useState<string | null>(null);
  const [name, setName] = useState('');
  const [category, setCategory] = useState('');
  const [description, setDescription] = useState('');
  const [maxDuration, setMaxDuration] = useState('');
  const [handover, setHandover] = useState('');
  const [submitting, setSubmitting] = useState(false);
  const [done, setDone] = useState(false);

  const handleSubmit = () => {
    setSubmitting(true);
    setTimeout(() => {
      setSubmitting(false);
      setDone(true);
    }, 1500);
  };

  if (done) {
    return (
      <div className="flex flex-col h-full items-center justify-center" style={{ background: '#F4F6FF' }}>
        <div className="flex flex-col items-center gap-6 px-8 text-center slide-up">
          <div
            className="flex items-center justify-center"
            style={{
              width: 100,
              height: 100,
              borderRadius: '50%',
              background: 'linear-gradient(135deg, #4F6AF0 0%, #7B61FF 100%)',
              boxShadow: '0 8px 32px rgba(79,106,240,0.35)',
            }}
          >
            <svg width="48" height="48" viewBox="0 0 48 48" fill="none">
              <path d="M10 24L20 34L38 16" stroke="white" strokeWidth="4" strokeLinecap="round" strokeLinejoin="round" className="check-draw" />
            </svg>
          </div>
          <div>
            <h2 style={{ fontSize: 26, fontWeight: 800, color: '#1C2135', margin: 0 }}>Item Listed!</h2>
            <p style={{ fontSize: 15, color: '#5A607A', marginTop: 8, lineHeight: 1.6 }}>
              Your item is now visible to students near you. You'll be notified when someone requests it.
            </p>
          </div>
          <button
            onClick={onDone}
            style={{
              width: '100%',
              height: 56,
              borderRadius: 18,
              background: 'linear-gradient(135deg, #4F6AF0 0%, #7B61FF 100%)',
              color: 'white',
              fontSize: 16,
              fontWeight: 700,
              border: 'none',
              cursor: 'pointer',
              boxShadow: '0 8px 24px rgba(79,106,240,0.35)',
            }}
          >
            Back to Home
          </button>
        </div>
        <HomeIndicator />
      </div>
    );
  }

  return (
    <div className="flex flex-col h-full" style={{ background: '#F4F6FF' }}>
      {/* Header */}
      <div
        className="px-5 pt-12 pb-5"
        style={{
          background: 'linear-gradient(160deg, #4F6AF0 0%, #7B61FF 100%)',
          borderBottomLeftRadius: 28,
          borderBottomRightRadius: 28,
        }}
      >
        <div className="flex items-center gap-4 mb-5">
          <button onClick={onBack} style={{ width: 40, height: 40, borderRadius: 12, background: 'rgba(255,255,255,0.2)', display: 'flex', alignItems: 'center', justifyContent: 'center', border: 'none', cursor: 'pointer' }}>
            <BackIcon size={20} color="white" />
          </button>
          <div>
            <h1 style={{ fontSize: 20, fontWeight: 800, color: 'white', margin: 0 }}>List an Item</h1>
            <p style={{ fontSize: 13, color: 'rgba(255,255,255,0.75)', margin: 0 }}>Step {step + 1} of {steps.length}</p>
          </div>
        </div>

        {/* Progress bar */}
        <div className="flex gap-1.5">
          {steps.map((s, i) => (
            <div key={s} className="flex-1 flex flex-col items-center gap-1.5">
              <div
                style={{
                  height: 4,
                  borderRadius: 2,
                  background: i <= step ? 'white' : 'rgba(255,255,255,0.3)',
                  transition: 'background 0.3s ease',
                }}
              />
              <span style={{ fontSize: 9, color: i <= step ? 'white' : 'rgba(255,255,255,0.5)', fontWeight: 600 }}>{s}</span>
            </div>
          ))}
        </div>
      </div>

      {/* Content */}
      <div className="flex-1 screen-scroll px-5 pt-6 pb-4">
        {step === 0 && (
          <div className="flex flex-col gap-4 slide-up">
            <div>
              <h2 style={{ fontSize: 20, fontWeight: 800, color: '#1C2135', margin: '0 0 4px' }}>Add a photo</h2>
              <p style={{ fontSize: 14, color: '#9BA3B8', margin: 0 }}>A good photo gets more borrow requests</p>
            </div>

            {/* Upload area */}
            <div
              className="flex flex-col items-center justify-center gap-4"
              style={{
                height: 200,
                borderRadius: 20,
                border: `2px dashed ${photo ? '#4F6AF0' : '#E5E8F2'}`,
                background: photo ? '#EEF1FF' : 'white',
                cursor: 'pointer',
                transition: 'all 0.2s ease',
              }}
              onClick={() => setPhoto('https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?w=400&h=300&fit=crop&auto=format')}
            >
              {photo ? (
                <img src={photo} alt="Preview" style={{ width: '100%', height: '100%', objectFit: 'cover', borderRadius: 18 }} />
              ) : (
                <>
                  <div style={{ width: 60, height: 60, borderRadius: 18, background: '#EEF1FF', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                    <CameraIcon size={28} color="#4F6AF0" />
                  </div>
                  <div className="text-center">
                    <p style={{ fontSize: 14, fontWeight: 700, color: '#1C2135', margin: 0 }}>Tap to upload photo</p>
                    <p style={{ fontSize: 12, color: '#9BA3B8', margin: '4px 0 0' }}>JPG, PNG up to 10MB</p>
                  </div>
                </>
              )}
            </div>

            {photo && (
              <button
                onClick={() => setPhoto(null)}
                style={{ fontSize: 13, fontWeight: 600, color: '#FF7A7A', background: 'none', border: 'none', cursor: 'pointer' }}
              >
                Remove photo
              </button>
            )}
          </div>
        )}

        {step === 1 && (
          <div className="flex flex-col gap-5 slide-up">
            <div>
              <h2 style={{ fontSize: 20, fontWeight: 800, color: '#1C2135', margin: '0 0 4px' }}>Item Details</h2>
              <p style={{ fontSize: 14, color: '#9BA3B8', margin: 0 }}>Help others find your item</p>
            </div>

            {/* Name */}
            <div>
              <label style={{ fontSize: 12, fontWeight: 600, color: '#5A607A', marginBottom: 6, display: 'block' }}>Item Name *</label>
              <input
                value={name}
                onChange={e => setName(e.target.value)}
                placeholder="e.g. Casio FX-991EX Calculator"
                style={{ width: '100%', height: 52, borderRadius: 14, border: '1.5px solid #E5E8F2', background: 'white', padding: '0 16px', fontSize: 15, fontWeight: 500, color: '#1C2135', boxShadow: 'var(--bb-shadow-sm)' }}
              />
            </div>

            {/* Category */}
            <div>
              <label style={{ fontSize: 12, fontWeight: 600, color: '#5A607A', marginBottom: 8, display: 'block' }}>Category *</label>
              <div className="flex flex-wrap gap-2">
                {categories.filter(c => c.id !== 'all').map(cat => (
                  <button
                    key={cat.id}
                    onClick={() => setCategory(cat.id)}
                    style={{
                      padding: '8px 14px',
                      borderRadius: 12,
                      background: category === cat.id ? '#4F6AF0' : 'white',
                      border: `1.5px solid ${category === cat.id ? '#4F6AF0' : '#E5E8F2'}`,
                      fontSize: 13,
                      fontWeight: 700,
                      color: category === cat.id ? 'white' : '#5A607A',
                      cursor: 'pointer',
                      transition: 'all 0.2s ease',
                      display: 'flex',
                      alignItems: 'center',
                      gap: 6,
                    }}
                  >
                    <span>{cat.icon}</span> {cat.label}
                  </button>
                ))}
              </div>
            </div>

            {/* Description */}
            <div>
              <label style={{ fontSize: 12, fontWeight: 600, color: '#5A607A', marginBottom: 6, display: 'block' }}>Description</label>
              <textarea
                value={description}
                onChange={e => setDescription(e.target.value)}
                placeholder="Describe the item condition, what's included..."
                rows={3}
                style={{ width: '100%', borderRadius: 14, border: '1.5px solid #E5E8F2', background: 'white', padding: 14, fontSize: 14, fontWeight: 500, color: '#1C2135', resize: 'none', fontFamily: 'inherit', boxShadow: 'var(--bb-shadow-sm)' }}
              />
            </div>
          </div>
        )}

        {step === 2 && (
          <div className="flex flex-col gap-5 slide-up">
            <div>
              <h2 style={{ fontSize: 20, fontWeight: 800, color: '#1C2135', margin: '0 0 4px' }}>Preferences</h2>
              <p style={{ fontSize: 14, color: '#9BA3B8', margin: 0 }}>Set your borrowing terms</p>
            </div>

            {/* Max duration */}
            <div>
              <label style={{ fontSize: 12, fontWeight: 600, color: '#5A607A', marginBottom: 8, display: 'block' }}>Maximum borrow duration</label>
              <div className="flex flex-wrap gap-2">
                {durations.map(d => (
                  <button
                    key={d}
                    onClick={() => setMaxDuration(d)}
                    style={{
                      padding: '8px 14px',
                      borderRadius: 12,
                      background: maxDuration === d ? '#4F6AF0' : 'white',
                      border: `1.5px solid ${maxDuration === d ? '#4F6AF0' : '#E5E8F2'}`,
                      fontSize: 13,
                      fontWeight: 700,
                      color: maxDuration === d ? 'white' : '#5A607A',
                      cursor: 'pointer',
                      transition: 'all 0.2s ease',
                    }}
                  >
                    {d}
                  </button>
                ))}
              </div>
            </div>

            {/* Handover method */}
            <div>
              <label style={{ fontSize: 12, fontWeight: 600, color: '#5A607A', marginBottom: 8, display: 'block' }}>Preferred handover location</label>
              <div className="flex flex-col gap-2">
                {handoverMethods.map(m => (
                  <button
                    key={m}
                    onClick={() => setHandover(m)}
                    className="flex items-center gap-3 p-4"
                    style={{
                      borderRadius: 14,
                      background: handover === m ? '#EEF1FF' : 'white',
                      border: `1.5px solid ${handover === m ? '#4F6AF0' : '#E5E8F2'}`,
                      cursor: 'pointer',
                      transition: 'all 0.2s ease',
                    }}
                  >
                    <div
                      style={{
                        width: 22,
                        height: 22,
                        borderRadius: '50%',
                        border: `2px solid ${handover === m ? '#4F6AF0' : '#E5E8F2'}`,
                        background: handover === m ? '#4F6AF0' : 'white',
                        display: 'flex',
                        alignItems: 'center',
                        justifyContent: 'center',
                        transition: 'all 0.2s ease',
                        flexShrink: 0,
                      }}
                    >
                      {handover === m && <div style={{ width: 8, height: 8, borderRadius: '50%', background: 'white' }} />}
                    </div>
                    <span style={{ fontSize: 14, fontWeight: 600, color: handover === m ? '#4F6AF0' : '#1C2135' }}>{m}</span>
                  </button>
                ))}
              </div>
            </div>
          </div>
        )}

        {step === 3 && (
          <div className="flex flex-col gap-4 slide-up">
            <div>
              <h2 style={{ fontSize: 20, fontWeight: 800, color: '#1C2135', margin: '0 0 4px' }}>Preview</h2>
              <p style={{ fontSize: 14, color: '#9BA3B8', margin: 0 }}>How your listing will look</p>
            </div>

            {/* Preview card */}
            <div style={{ background: 'white', borderRadius: 24, overflow: 'hidden', border: '1px solid #F0F2FA', boxShadow: 'var(--bb-shadow)' }}>
              <div style={{ height: 180, background: '#EEF1FF', position: 'relative' }}>
                {photo && <img src={photo} alt="Preview" style={{ width: '100%', height: '100%', objectFit: 'cover' }} />}
                {!photo && (
                  <div className="flex items-center justify-center h-full">
                    <span style={{ fontSize: 13, color: '#9BA3B8' }}>No photo added</span>
                  </div>
                )}
                <div className="absolute bottom-3 left-3">
                  <span style={{ fontSize: 11, fontWeight: 700, color: '#3BB8A2', background: 'rgba(230,247,244,0.95)', borderRadius: 8, padding: '3px 10px' }}>● Available</span>
                </div>
              </div>
              <div className="p-4">
                <p style={{ fontSize: 17, fontWeight: 800, color: '#1C2135', margin: '0 0 4px' }}>
                  {name || 'Item Name'}
                </p>
                <p style={{ fontSize: 13, color: '#9585E0', fontWeight: 600, margin: '0 0 8px' }}>
                  {category ? categories.find(c => c.id === category)?.label : 'Category'}
                </p>
                <p style={{ fontSize: 13, color: '#5A607A', lineHeight: 1.5, margin: '0 0 12px' }}>
                  {description || 'No description added.'}
                </p>
                <div className="flex gap-3 text-xs" style={{ color: '#9BA3B8', fontWeight: 500 }}>
                  <span>⏱ Max: {maxDuration || '-'}</span>
                  <span>📍 {handover || '-'}</span>
                </div>
              </div>
            </div>

            <div
              className="flex items-start gap-3 p-4"
              style={{ background: '#EEF1FF', borderRadius: 16 }}
            >
              <span style={{ fontSize: 18 }}>💡</span>
              <p style={{ fontSize: 13, color: '#4F6AF0', fontWeight: 500, margin: 0, lineHeight: 1.5 }}>
                Your listing will be visible to students within 2km of your college. You can edit or deactivate it anytime.
              </p>
            </div>
          </div>
        )}
      </div>

      {/* Navigation */}
      <div className="flex-shrink-0 px-5 pt-3 pb-2" style={{ background: 'white', borderTop: '1px solid #E5E8F2' }}>
        <div className="flex gap-3">
          {step > 0 && (
            <button
              onClick={() => setStep(step - 1)}
              style={{
                height: 56,
                paddingLeft: 20,
                paddingRight: 20,
                borderRadius: 18,
                background: '#F4F6FF',
                color: '#5A607A',
                fontSize: 15,
                fontWeight: 700,
                border: '1.5px solid #E5E8F2',
                cursor: 'pointer',
              }}
            >
              Back
            </button>
          )}
          <button
            onClick={step < steps.length - 1 ? () => setStep(step + 1) : handleSubmit}
            style={{
              flex: 1,
              height: 56,
              borderRadius: 18,
              background: submitting ? '#9BA3B8' : 'linear-gradient(135deg, #4F6AF0 0%, #7B61FF 100%)',
              color: 'white',
              fontSize: 16,
              fontWeight: 700,
              border: 'none',
              cursor: submitting ? 'not-allowed' : 'pointer',
              boxShadow: submitting ? 'none' : '0 8px 24px rgba(79,106,240,0.35)',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              gap: 8,
            }}
          >
            {submitting ? (
              <>
                <div style={{ width: 20, height: 20, border: '2px solid rgba(255,255,255,0.4)', borderTopColor: 'white', borderRadius: '50%', animation: 'spin 0.8s linear infinite' }} />
                Listing...
              </>
            ) : (
              step < steps.length - 1 ? 'Continue →' : 'List Item 🎉'
            )}
          </button>
        </div>
      </div>
      <HomeIndicator />
      <style>{`@keyframes spin { to { transform: rotate(360deg); } }`}</style>
    </div>
  );
};
