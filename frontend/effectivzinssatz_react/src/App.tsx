import React, { useState, useRef, useEffect } from 'react';
import uniquaLogo from '/UniquaLogo.jpg';

import './App.css'

function App() {
  const [laufzeit, setLaufzeit] = useState<number|"">(0);
  const [einzahlungsdauer, setEinzahlungsdauer] = useState<number|"">(0);
  const [zahlungsrhythmus, setZahlungsrhythmus] = useState<string>('Jährlich');
  const [zahlungenProJahr, setZahlungenProJahr] = useState<number>(1);
  const [einzahlungHoehe, setEinzahlungHoehe] = useState<number|"">(0 );
  const [endBetrag, setEndBetrag] = useState<number|"">(0);
  const [effektivzins, setEffektivzins] = useState<number>(1);
  const [fehlermeldungen, setFehlermeldungen] = useState<{ id: number; text: string }[]>([]);
  const [popupPosition, setPopupPosition] = useState({ top: 0, left: 0 });
  const buttonRef = useRef<HTMLButtonElement | null>(null);
  const popupContainerRef = useRef<HTMLDivElement | null>(null);
  const handleBerechnen = () => {
      if(laufzeit == "" || einzahlungsdauer == "" || einzahlungHoehe == "" || endBetrag == "" ){
          // Berechne die Position des Buttons
          berechneButtonPosition();
          addFehlerMeldung("Ein oder mehrere Werte sind nicht gesetzt.");
          return; // Stoppt die Berechnung
      }
      if(laufzeit < 0 || einzahlungsdauer < 0 || einzahlungHoehe < 0 || endBetrag < 0 ){
          // Berechne die Position des Buttons
          berechneButtonPosition();
          addFehlerMeldung("Ein oder mehrere Werte sind kleiner 0.");
          return; // Stoppt die Berechnung
      }
      // Überprüfung: Sparlaufzeit >= Einzahlungsdauer
      if ( einzahlungHoehe*laufzeit*zahlungenProJahr  > endBetrag) {
          berechneButtonPosition();
          addFehlerMeldung("Die Summe der Einzahlungen über die Laufzeit ist größer als der gewünschte Endbetrag. Bitte überprüfen Sie die Eingabewerte.");
          return; // Stoppt die Berechnung
      }

      if ( laufzeit  < einzahlungsdauer) {
          berechneButtonPosition();
          addFehlerMeldung("Die gesamte Sparlaufzeit darf nicht kleiner sein als die Einzahlungsdauer.");
          return; // Stoppt die Berechnung
      }
      // Die Daten für den POST-Request
      const requestData = {
          laufzeit: laufzeit,
          einzahlungsDauer: einzahlungsdauer,
          zahlungenProJahr: zahlungenProJahr,
          einzahlungsHoehe: einzahlungHoehe,
          endBetrag: endBetrag
      };

      // POST-Request an den Server
      fetch("http://localhost:8080/rate/effZins", {
          method: "POST",
          headers: {
              "Content-Type": "application/json"
          },
          body: JSON.stringify(requestData),
      })
          .then(response => response.json())
          .then(data => {
              const zinssatz = data.zinssatz;
              setEffektivzins(zinssatz); // Beispielrechnung
          })
          .catch(error => {
              // Fehlerbehandlung
              console.error("Fehler bei der Anfrage:", error);
              addFehlerMeldung("Ein Fehler ist aufgetreten. Bitte versuche es später erneut.");
          });
      };
      const addFehlerMeldung = (meldung: string) => {
          const id = Date.now(); // Eindeutige ID
          setFehlermeldungen((prev) => [...prev, { id, text: meldung }]);

          // Automatisches Entfernen nach 10 Sekunden
          setTimeout(() => {
              setFehlermeldungen((prev) => prev.filter((error) => error.id !== id));
          }, 10000);
      };
      // Funktion zum Berechnen der Button-Position
      const berechneButtonPosition = () => {
          if (buttonRef.current) {
              const rect = buttonRef.current.getBoundingClientRect();
              setPopupPosition({
                  top: rect.bottom + window.scrollY, // Position unterhalb des Buttons
                  left: rect.left + window.scrollX,   // Position entsprechend der linken Seite
              });
      }
  };
  const handleZahlungsrhythmusChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
      const selectedRhythmus = e.target.value;
      setZahlungsrhythmus(selectedRhythmus);

      // Mapping der Auswahl auf die Anzahl der Zahlungen pro Jahr
      switch (selectedRhythmus) {
          case 'Jährlich':
              setZahlungenProJahr(1);
              break;
          case 'Halbjährlich':
              setZahlungenProJahr(2);
              break;
          case 'Quartalsweise':
              setZahlungenProJahr(4);
              break;
          case 'Monatlich':
              setZahlungenProJahr(12);
              break;
          case 'Einmalig':
              setZahlungenProJahr(0);
              break;
          default:
              setZahlungenProJahr(12); // Standardwert
      }
  };
  useEffect(() => {
      // Optional: Wenn der Popup Container ein bestimmtes Verhalten hat
      if (popupContainerRef.current) {
          popupContainerRef.current.style.position = 'absolute';
          popupContainerRef.current.style.top = `${popupPosition.top}px`;
          popupContainerRef.current.style.left = `${popupPosition.left}px`;
      }
  }, [popupPosition]);
    return (
        <div className="App">
            <header>
                <img src={uniquaLogo} alt="Uniqua Logo" className="header-logo" />
                <h1>Effektivzinssatz Rechner</h1>
            </header>
            <div className="form-container">
                <div className="form-group">
                    <label>Gesamte Sparlaufzeit in Jahren</label>
                    <input
                        type="number"
                        value={laufzeit}
                        onChange={(e) => {
                            // Verhindert, dass bei Eingabe von leeren Feldern oder ungültigen Eingaben eine 0 gesetzt wird
                            const newValue = e.target.value;
                            setLaufzeit(newValue ? Number(e.target.value) : ""); // Nur wenn der Wert nicht leer ist, setzen wir den Wert
                        }}
                        onFocus={(e) => e.target.select()}
                    />
                </div>
                <div className="form-group">
                    <label>Einzahlungsdauer in Jahren</label>
                    <input
                        type="number"
                        value={einzahlungsdauer}
                        onChange={(e) => {
                            // Verhindert, dass bei Eingabe von leeren Feldern oder ungültigen Eingaben eine 0 gesetzt wird
                            const newValue = e.target.value;
                            setEinzahlungsdauer(newValue ? Number(e.target.value) : ""); // Nur wenn der Wert nicht leer ist, setzen wir den Wert
                        }}
                        onFocus={(e) => e.target.select()}
                    />
                </div>
                <div className="form-group">
                    <label>Zahlungsrhythmus</label>
                    <select
                        value={zahlungsrhythmus}
                        onChange={handleZahlungsrhythmusChange}
                    >
                        <option value="Jährlich">Jährlich</option>
                        <option value="Halbjährlich">Halbjährlich</option>
                        <option value="Quartalsweise">Quartalsweise</option>
                        <option value="Monatlich">Monatlich</option>
                        <option value="Einmalig">Einmalig</option>
                    </select>
                </div>
                <div className="form-group">
                    <label>Einzahlungshöhe in Euro</label>
                    <input
                        type="number"
                        value={einzahlungHoehe}
                        step="50"
                        onChange={(e) => {
                            // Verhindert, dass bei Eingabe von leeren Feldern oder ungültigen Eingaben eine 0 gesetzt wird
                            const newValue = e.target.value;
                            setEinzahlungHoehe(newValue ? parseFloat(newValue) : ""); // Nur wenn der Wert nicht leer ist, setzen wir den Wert
                        }}
                        onFocus={(e) => e.target.select()}
                    />
                </div>
                <div className="form-group">
                    <label>End-Betrag nach gesamter Sparlaufzeit in Euro</label>
                    <input
                        type="number"
                        value={endBetrag}
                        step="500"
                        onChange={(e) => {
                            // Verhindert, dass bei Eingabe von leeren Feldern oder ungültigen Eingaben eine 0 gesetzt wird
                            const newValue = e.target.value;
                            setEndBetrag(newValue ? parseFloat(newValue) : ""); // Nur wenn der Wert nicht leer ist, setzen wir den Wert
                        }}
                        onFocus={(e) => e.target.select()}
                    />
                </div>
                <button ref={buttonRef} onClick={handleBerechnen}>Effektivzinssatz berechnen</button>
                {/* Fehlermeldung anzeigen */}
                <div className="popup-container"  ref={popupContainerRef} style={{ position: 'absolute', top: `${popupPosition.top}px`, left: `${popupPosition.left}px` }} >
                    {fehlermeldungen.map((error) => (
                        <div key={error.id} className="popup">
                            {error.text}
                        </div>
                    ))}
                </div>
                <div className="result">
                    <h3>Ergebnisse</h3>
                    <p>Effektivzinssatz: {((effektivzins-1)*100).toFixed(2)}%</p>
                </div>
            </div>
        </div>
    )
}

export default App
