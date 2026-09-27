import { createAuth0Client } from './lib/auth0-spa-js.js';

let auth0Client;

const callApiBtn = document.getElementById("call-api");
const loginBtn = document.getElementById("login");
const logoutBtn = document.getElementById("logout")
const addUserBtn = document.getElementById("add-user");

async function initAuth0() {
  try {
    auth0Client = await createAuth0Client({
      domain: 'dev-iamssa85wl4jrzqy.us.auth0.com',
      clientId: 'lP9XqtVpTjWebnkhJwj3olEicDGaouJ9',
      authorizationParams: {
        redirect_uri: window.location.origin + "/home.html",
        audience: 'https://spring-security-api',
      }
    });

    if (window.location.search.includes('code=') && window.location.search.includes('state=')) {
        await handleRedirectCallback();
    }
    
    const isLoggedIn = await auth0Client.isAuthenticated();
    if (isLoggedIn) {
      callApiBtn.removeAttribute('hidden');
      logoutBtn.removeAttribute('hidden');
      const accessToken = await auth0Client.getTokenSilently();
      const result = await fetch('/api/users/add', {
        method: 'GET',
        headers: {
          Authorization: 'Bearer ' + accessToken
        }
      });
      console.log(result);
    }

    await updateUI();
    } catch (err) {
    }
}

async function handleRedirectCallback() {
  try {
    await auth0Client.handleRedirectCallback();
    window.history.replaceState({}, document.title, window.location.pathname);
  } catch (err) {
  }
}

async function login() {
  try {
    await auth0Client.loginWithRedirect();
  } catch (err) {
  }
}

async function logout() {
  try {
    await auth0Client.logout({
      logoutParams: {
        returnTo: window.location.origin + "/home.html"
      }
    });
  } catch (err) {
  }
}

loginBtn.addEventListener('click', login);
logoutBtn.addEventListener('click', logout);

callApiBtn.addEventListener('click', async () => {
  const accessToken = await auth0Client.getTokenSilently();
  const result = await fetch('/api/users/me', {
    method: 'GET',
    headers: {
      Authorization: 'Bearer ' + accessToken
    }
  });
  const data = await result.json();
  console.log(data);
  const claims = await auth0Client.getIdTokenClaims();
  console.log(claims);
});

initAuth0();