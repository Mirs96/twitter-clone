import { HttpInterceptorFn } from '@angular/common/http';

//sino all'anno scorso erano classi, angular si sta spostando a un approccio funzionale, copiando react
//perchè la maggior parte delle persone vorrebbe js più funzionale
export const authInterceptor: HttpInterceptorFn = (req, next) => { //req = richiesta in uscita da intercettare, 
  //next = prossimo elemento nella catena degli intercettatori
  const token = localStorage.getItem('jwtToken');
  if(token) {
    req = req.clone({ //creo una richiesta identica a quella di prima ma gli setto io l'header col token
      setHeaders: {
        authorization: `Bearer ${token}`
      }
    });
  }
  return next(req); //passo questa richiesta al prossimo interceptor, appena finiscono la richiesta va subito al backend
};
