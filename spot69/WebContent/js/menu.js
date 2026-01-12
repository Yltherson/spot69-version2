function showSection(type) {
	document.getElementById("menu-selection").classList.add("hide");
	document.getElementById("food-section").classList.add("hide");
	document.getElementById("drinks-section").classList.add("hide");

	document.querySelector(".menu.section").style.marginTop = "0px";
	document.getElementById("main-title").classList.add("hide");

	if (type === 'food') {
		// Activer le premier tab uniquement
		const tabButtons = document.querySelectorAll("#food-section .tab-btn");
		const tabContents = document.querySelectorAll("#food-section .tab-content");

		tabButtons.forEach((btn, index) => {
			btn.classList.toggle("active", index === 0);
		});
		tabContents.forEach((content, index) => {
			content.classList.toggle("active", index === 0);
		});

		document.getElementById("food-section").classList.remove("hide");
	} else if (type === 'drinks') {
		document.getElementById("drinks-section").classList.remove("hide");
	}
}


function goBack() {
	document.getElementById("menu-selection").classList.remove("hide");
	document.getElementById("food-section").classList.add("hide");
	document.getElementById("drinks-section").classList.add("hide");
}

// Gestion des onglets plats
const tabButtons = document.querySelectorAll(".tab-btn");
const contents = document.querySelectorAll(".tab-content");

tabButtons.forEach(btn => {
	btn.addEventListener("click", () => {
		tabButtons.forEach(b => b.classList.remove("active"));
		contents.forEach(c => c.classList.remove("active"));

		btn.classList.add("active");
		document.getElementById(btn.dataset.tab).classList.add("active");
	});
});